package br.com.legion.guilds.maintenance;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.shared.user.User;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.echo.packets.GuildDisbandPacket;
import br.com.legion.guilds.echo.packets.UserLeftGuildPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class GuildMaintenanceRunnable implements Runnable {

    @Getter
    private final Guild guild;

    @Override
    public void run() {
        if (!guild.needMaintenance()) {
            return;
        }

        int maintenanceTaxByLevel = GuildsConstants.Config.getMaintenanceTaxByLevel(guild.getLevel());

        Integer id = guild.getId();

        GuildsProvider.Repositories.GUILDS.provide().updateLastMaintenance(id, System.currentTimeMillis());
        GuildsProvider.Cache.Local.GUILDS.provide().invalidateId(id);

        if (guild.getGloryPoints() < maintenanceTaxByLevel) {
            if (guild.getLevel() == 1) {
                deleteGuild();
            } else {
                downgradeLevel();
            }
            return;
        }

        guild.setGloryPoints(guild.getGloryPoints() - maintenanceTaxByLevel);

        GuildsProvider.Repositories.GUILDS.provide().updateBank(id, guild.getGloryPoints());
        GuildsProvider.Cache.Local.GUILDS.provide().invalidateId(id);

        ComponentBuilder builder = new ComponentBuilder("\n")
                .color(ChatColor.GREEN)
                .append("A guilda acaba de pagar a taxa de manutenção.", ComponentBuilder.FormatRetention.NONE)
                .append("Valor pago foi de " + NumberUtils.format(maintenanceTaxByLevel) + " pontos de glória.")
                .append("\n");

        GuildUtils.broadcast(
                id,
                builder.create()
        );
    }

    private void deleteGuild() {
        ComponentBuilder builder = new ComponentBuilder("\n")
                .color(ChatColor.GREEN)
                .append("A guilda acaba receber uma penalização por não possuir pontos de glória em banco.", ComponentBuilder.FormatRetention.NONE)
                .append("A penalização é a exclusão da guilda.")
                .append("\n");

        GuildUtils.broadcast(
                guild.getId(),
                builder.create()
        );

        boolean result = GuildsProvider.Repositories.GUILDS.provide().delete(guild.getId());

        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateClan(guild.getId());
        GuildsProvider.Cache.Local.GUILDS.provide().invalidateId(guild.getId());

        if (!result) {
            return;
        }

        GuildsFrameworkProvider.Redis.ECHO.provide().publishToAll(
                new GuildDisbandPacket(guild.getId()));
    }

    private void downgradeLevel() {
        guild.setLevel(Math.min(1, guild.getLevel()));

        Integer id = guild.getId();

        GuildsProvider.Repositories.GUILDS.provide().updateLevel(id, guild.getLevel());
        GuildsProvider.Cache.Local.GUILDS.provide().invalidateId(id);

        ComponentBuilder builder = new ComponentBuilder("\n")
                .color(ChatColor.GREEN)
                .append("A guilda acaba receber uma penalização por não possuir pontos de glória em banco.", ComponentBuilder.FormatRetention.NONE)
                .append("A penalização é da redução de um level, ou seja, o level da guilda agora é " + guild.getLevel() + ".")
                .append("\n");

        GuildUtils.broadcast(
                id,
                builder.create()
        );

        Set<GuildUserRelation> byGuild = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByGuild(id);
        if (byGuild.size() > guild.getMaxMembers()) {
            int diff = byGuild.size() - guild.getMaxMembers();

            Date date = new Date(System.currentTimeMillis());
            AtomicInteger count = new AtomicInteger();

            byGuild.stream()
                    .limit(diff)
                    .filter(relation -> relation.getRole() == GuildRole.MEMBER)
                    .filter(relation -> {
                        long diffInTime = date.getTime() - relation.getSince().getTime();
                        long diffInDays = (diffInTime / (1000L * 60 * 60 * 24 * 365));
                        return diffInDays <= GuildsConstants.Config.MAINTENANCE_COOLDOWN;
                    })
                    .forEach(relation -> {
                        User user = ApiProvider.Cache.Local.USERS.provide().get(relation.getUserId());

                        GuildsProvider.Repositories.USERS_RELATIONS.provide().removeByUser(user.getId());

                        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(user.getId());
                        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateClan(relation.getGuildId());

                        GuildsFrameworkProvider.Redis.ECHO.provide().publishToAll(new UserLeftGuildPacket(
                                relation.getGuildId(),
                                user.getId(),
                                UserLeftGuildPacket.Reason.KICK
                        ));

                        count.getAndIncrement();
                    });

            ComponentBuilder builderKick = new ComponentBuilder("\n")
                    .color(ChatColor.GREEN)
                    .append("A guilda possuia mais membros do que o máximo permitido e por conta disto,", ComponentBuilder.FormatRetention.NONE)
                    .append("acabamos de expulsar " + count.get() + " membro(s) da guilda.")
                    .append("\n");

            GuildUtils.broadcast(
                    id,
                    builderKick.create()
            );
        }
    }
}
