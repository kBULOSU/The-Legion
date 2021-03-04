package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.echo.packets.UserRankUpdatedPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.server.ServerType;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

public class DemoteSubCommand extends GuildSubCommand {

    public DemoteSubCommand() {
        super("rebaixar", GuildRole.CAPTAIN, "demote", "demote");

        registerArgument(new NickArgument("nick", "Nick do jogador que será rebaixado"));
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        if (GuildsFrameworkProvider.getServerType() == ServerType.LOBBY) {
            Message.ERROR.send(player, "Este comando só pode ser usado no servidor principal.");
            return;
        }

        String targetNick = args[0];

        User targetUser = ApiProvider.Cache.Local.USERS.provide().get(targetNick);

        if (targetUser == null) {
            Message.ERROR.send(player, String.format("O jogador %s não foi encontrado.", targetNick));
            return;
        }

        if (targetUser.equals(user)) {
            Message.ERROR.send(player, "Você precisa escolher outro jogador para rebaixar.");
            return;
        }

        GuildUserRelation targetRelation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(targetUser.getId());

        if (targetRelation == null || targetRelation.getGuildId() != relation.getGuildId()) {
            Message.ERROR.send(player, String.format("O jogador %s não está em sua guilda.", targetNick));
            return;
        }

        if (targetRelation.getRole() == GuildRole.MEMBER) {
            Message.ERROR.send(player, String.format("Você não pode rebaixar um %s.", GuildRole.MEMBER.getDisplayName()));
            return;
        }

        GuildRole oldRank = targetRelation.getRole();

        if (relation.getRole().ordinal() <= oldRank.ordinal()) {
            Message.ERROR.send(player, "Você não pode rebaixar jogadores com o rank maior ou igual ao seu.");
            return;
        }

        GuildRole newRank = oldRank.previous();

        targetRelation.setRole(newRank);

        GuildsProvider.Repositories.USERS_RELATIONS.provide().update(targetRelation);
        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(targetUser.getId());

        GuildsFrameworkProvider.Redis.ECHO.provide().publishToCurrentServer(
                new UserRankUpdatedPacket(targetUser.getId(), user.getId(), oldRank, newRank));

        Message.SUCCESS.send(player, String.format(
                "FEITO! Você rebaixou o jogador %s para %s.",
                targetUser.getName(),
                newRank.getDisplayName()
        ));
    }
}
