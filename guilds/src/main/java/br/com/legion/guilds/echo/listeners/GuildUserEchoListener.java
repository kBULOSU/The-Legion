package br.com.legion.guilds.echo.listeners;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.messages.MessageUtils;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.echo.packets.UserInvitedGuildPacket;
import br.com.legion.guilds.echo.packets.UserJoinedGuildPacket;
import br.com.legion.guilds.echo.packets.UserLeftGuildPacket;
import br.com.legion.guilds.echo.packets.UserRankUpdatedPacket;
import br.com.legion.guilds.framework.echo.api.EchoListener;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

public class GuildUserEchoListener implements EchoListener {

    @Subscribe
    public void on(UserJoinedGuildPacket packet) {
        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateClan(packet.getGuildId());
        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(packet.getUserId());

        if (packet.getReason() == UserJoinedGuildPacket.Reason.INVITATION) {
            ComponentBuilder builder = new ComponentBuilder("");

            User targetUser = ApiProvider.Cache.Local.USERS.provide().get(packet.getUserId());

            builder.append(targetUser.getName())
                    .append(" entrou em sua guilda!", ComponentBuilder.FormatRetention.NONE)
                    .color(ChatColor.GREEN);

            GuildUtils.broadcast(packet.getGuildId(), builder.create(), true);
        }
    }

    @Subscribe
    public void on(UserLeftGuildPacket packet) {
        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateClan(packet.getGuildId());
        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(packet.getUserId());

        User targetUser = ApiProvider.Cache.Local.USERS.provide().get(packet.getUserId());
        Player player = Bukkit.getPlayerExact(targetUser.getName());

        if (player != null && player.isOnline() && packet.getReason() == UserLeftGuildPacket.Reason.KICK) {
            Message.ERROR.send(player, "Você foi expulso da guilda.");
        }

        ComponentBuilder builder = new ComponentBuilder("")
                .append(targetUser.getName())
                .color(ChatColor.RED);

        if (packet.getReason() == UserLeftGuildPacket.Reason.LEAVE) {
            builder.reset()
                    .append(" saiu da sua guilda.", ComponentBuilder.FormatRetention.NONE)
                    .color(ChatColor.RED);

            GuildUtils.broadcast(packet.getGuildId(), builder.create(), true);

        } else if (packet.getReason() == UserLeftGuildPacket.Reason.KICK) {
            builder.reset()
                    .append(" foi expulso da sua guilda.", ComponentBuilder.FormatRetention.NONE)
                    .color(ChatColor.RED);

            GuildUtils.broadcast(packet.getGuildId(), builder.create(), true);
        }
    }

    @Subscribe
    public void on(UserInvitedGuildPacket packet) {
        User targetUser = ApiProvider.Cache.Local.USERS.provide().get(packet.getTargetUserId());
        Player player = Bukkit.getPlayerExact(targetUser.getName());

        if (player == null || !player.isOnline()) {
            return;
        }

        Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(packet.getGuildId());

        ComponentBuilder builder = new ComponentBuilder("Você foi convidado para a guilda " + guild.getTag().toUpperCase())
                .color(ChatColor.YELLOW)
                .append("\n")
                .append("Clique para ")
                .color(ChatColor.YELLOW)
                .append("ACEITAR")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guilda aceitar " + guild.getTag()))
                .event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{
                                new TextComponent(MessageUtils.translateColorCodes(String.format(
                                        "&aClique e aceite o convite para\n&aentrar na guilda %s.", guild.getTag().toUpperCase()
                                )))
                        }
                ))
                .color(ChatColor.GREEN)
                .bold(true)
                .append(".")
                .color(ChatColor.YELLOW);

        player.spigot().sendMessage(builder.create());
    }

    @Subscribe
    public void on(UserRankUpdatedPacket packet) {
        if (packet.getNewRank() != GuildRole.LEADER && packet.getOldRank() != GuildRole.LEADER) {
            User targetUser = ApiProvider.Cache.Local.USERS.provide().get(packet.getTargetUserId());
            GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(packet.getTargetUserId());

            GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateClan(relation.getGuildId());
            GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(targetUser.getId());

            ComponentBuilder builder = new ComponentBuilder("")
                    .append(targetUser.getName());

            if (packet.getNewRank().isHigher(packet.getOldRank())) {
                builder.append(" foi promovido a ", ComponentBuilder.FormatRetention.NONE)
                        .color(ChatColor.GREEN)
                        .append(packet.getNewRank().getDisplayName())
                        .append("!");
            } else {
                builder.append(" foi rebaixado a ", ComponentBuilder.FormatRetention.NONE)
                        .color(ChatColor.RED)
                        .append(packet.getNewRank().getDisplayName())
                        .append("!");
            }

            GuildUtils.broadcast(relation.getGuildId(), builder.create(), true);
        }
    }
}
