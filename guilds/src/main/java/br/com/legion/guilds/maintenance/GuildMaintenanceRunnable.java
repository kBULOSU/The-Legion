package br.com.legion.guilds.maintenance;

import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.echo.packets.GuildDisbandPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.misc.utils.GuildUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

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

        if (guild.getGloryPoints() < maintenanceTaxByLevel) {
            if (guild.getLevel() == 1) {
                deleteGuild();
            } else {
                downgradeLevel();
            }
            return;
        }

        guild.setGloryPoints(guild.getGloryPoints() - maintenanceTaxByLevel);
        GuildsProvider.Repositories.GUILDS.provide().updateBank(guild.getId(), guild.getGloryPoints());

        ComponentBuilder builder = new ComponentBuilder("\n")
                .color(ChatColor.GREEN)
                .append("A guilda acaba de pagar a taxa de manutenção.", ComponentBuilder.FormatRetention.NONE)
                .append("Valor pago foi de " + NumberUtils.format(maintenanceTaxByLevel) + " pontos de glória.")
                .append("\n");

        GuildUtils.broadcast(
                guild.getId(),
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

        ComponentBuilder builder = new ComponentBuilder("\n")
                .color(ChatColor.GREEN)
                .append("A guilda acaba receber uma penalização por não possuir pontos de glória em banco.", ComponentBuilder.FormatRetention.NONE)
                .append("A penalização é de redução de um level, ou seja, o level da guilda agora é " + guild.getLevel() + ".")
                .append("\n");

        GuildUtils.broadcast(
                guild.getId(),
                builder.create()
        );
    }
}
