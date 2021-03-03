package br.com.legion.guilds.echo.listeners;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.messages.MessageUtils;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.echo.packets.GuildDisbandPacket;
import br.com.legion.guilds.echo.packets.relation.GuildRelationCreatedPacket;
import br.com.legion.guilds.echo.packets.relation.GuildRelationDeletedPacket;
import br.com.legion.guilds.echo.packets.relation.GuildRelationInvitePacket;
import br.com.legion.guilds.framework.echo.api.EchoListener;
import br.com.legion.guilds.relation.guild.GuildRelation;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

public class GuildEchoListener implements EchoListener {

    @Subscribe
    public void on(GuildDisbandPacket packet) {
        GuildsProvider.Cache.Local.GUILDS.provide().invalidateId(packet.getGuildId());
    }

    @Subscribe
    public void on(GuildRelationInvitePacket packet) {
        int targetClanId = packet.getTargetGuildId();
        GuildUserRelation targetLeaderRelation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide()
                .getByGuildAndRole(targetClanId, GuildRole.LEADER);

        if (targetLeaderRelation == null) {
            return;
        }

        User targetLeader = ApiProvider.Cache.Local.USERS.provide().get(targetLeaderRelation.getUserId());
        Player targetPlayer;

        if (targetLeader == null || (targetPlayer = Bukkit.getPlayerExact(targetLeader.getName())) == null || !targetPlayer.isOnline()) {
            return;
        }

        Guild senderGuild = GuildsProvider.Cache.Local.GUILDS.provide().getById(packet.getSenderGuildId());
        GuildsProvider.Cache.Local.GUILDS_RELATIONS.provide().invalidate(packet.getSenderGuildId(), packet.getTargetGuildId());

        ComponentBuilder builder = new ComponentBuilder("\nSua guilda foi convidada para uma aliança com a guilda " + senderGuild.getTag().toUpperCase())
                .color(ChatColor.YELLOW)
                .append("\n")
                .append("Clique para ")
                .color(ChatColor.YELLOW)
                .append("ACEITAR")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guilda ally " + senderGuild.getTag()))
                .event(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{
                                new TextComponent(MessageUtils.translateColorCodes(String.format(
                                        "&aClique e aceite o convite \n&ade aliança de %s.", senderGuild.getTag().toUpperCase()
                                )))
                        }
                ))
                .color(ChatColor.GREEN)
                .bold(true)
                .append(".\n")
                .color(ChatColor.YELLOW);

        targetPlayer.spigot().sendMessage(builder.create());
    }

    @Subscribe
    public void on(GuildRelationCreatedPacket packet) {
        int targetClanId = packet.getTargetGuildId();
        GuildUserRelation targetLeaderRelation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().
                getByGuildAndRole(targetClanId, GuildRole.LEADER);

        if (targetLeaderRelation == null) {
            return;
        }

        User targetLeader = ApiProvider.Cache.Local.USERS.provide().get(targetLeaderRelation.getUserId());
        Player targetPlayer;

        if (targetLeader == null || (targetPlayer = Bukkit.getPlayerExact(targetLeader.getName())) == null || !targetPlayer.isOnline()) {
            return;
        }

        Guild senderGuild = GuildsProvider.Cache.Local.GUILDS.provide().getById(packet.getSenderGuildId());
        GuildsProvider.Cache.Local.GUILDS_RELATIONS.provide().invalidate(packet.getSenderGuildId(), packet.getTargetGuildId());

        if (packet.getRelation() == GuildRelation.ALLY) {
            Message.SUCCESS.send(targetPlayer, String.format("Sua guilda começou uma aliança com a guilda %s&a.", senderGuild.getDisplayName()));
        }
    }

    @Subscribe
    public void on(GuildRelationDeletedPacket packet) {
        int targetClanId = packet.getTargetGuildId();
        GuildUserRelation targetLeaderRelation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide()
                .getByGuildAndRole(targetClanId, GuildRole.LEADER);

        if (targetLeaderRelation == null) {
            return;
        }

        User targetLeader = ApiProvider.Cache.Local.USERS.provide().get(targetLeaderRelation.getUserId());
        Player targetPlayer;

        if (targetLeader == null || (targetPlayer = Bukkit.getPlayerExact(targetLeader.getName())) == null || !targetPlayer.isOnline()) {
            return;
        }

        Guild senderGuild = GuildsProvider.Cache.Local.GUILDS.provide().getById(packet.getSenderGuildId());
        GuildsProvider.Cache.Local.GUILDS_RELATIONS.provide().invalidate(packet.getSenderGuildId(), packet.getTargetGuildId());

        if (packet.getRelation() == GuildRelation.ALLY) {
            Message.INFO.send(targetPlayer, String.format("A aliança com a guilda %s &efoi desfeita.", senderGuild.getDisplayName()));
        }
    }
}