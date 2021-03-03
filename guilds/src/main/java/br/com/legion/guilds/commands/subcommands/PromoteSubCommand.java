package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.echo.packets.UserRankUpdatedPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

public class PromoteSubCommand extends GuildSubCommand {

    public PromoteSubCommand() {
        super("promover", GuildRole.CAPTAIN, "promote");

        registerArgument(new NickArgument("nick", "Nick do jogador que será promovido."));
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        String targetNick = args[0];

        User targetUser = ApiProvider.Cache.Local.USERS.provide().get(targetNick);

        if (targetUser == null) {
            Message.ERROR.send(player, String.format("O jogador %s não foi encontrado.", targetNick));
            return;
        }

        if (targetUser.equals(user)) {
            Message.ERROR.send(player, "Você precisa escolher outro jogador para promover.");
            return;
        }

        GuildUserRelation targetRelation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(targetUser.getId());

        if (targetRelation == null || targetRelation.getGuildId() != relation.getGuildId()) {
            Message.ERROR.send(player, String.format("O jogador %s não está na sua guilda.", targetNick));
            return;
        }

        GuildRole oldRank = targetRelation.getRole();

        if (relation.getRole().ordinal() <= oldRank.ordinal()) {
            Message.ERROR.send(player, "Você não pode promover jogadores com o rank maior ou igual ao seu.");
            return;
        }

        GuildRole newRank = oldRank.next();

        if (relation.getRole() == GuildRole.CAPTAIN && newRank == GuildRole.CAPTAIN) {
            Message.ERROR.send(player, String.format("Apenas o %s pode promover um %s para %s.",
                    GuildRole.LEADER.getDisplayName(),
                    GuildRole.MEMBER.getDisplayName(),
                    GuildRole.CAPTAIN.getDisplayName()
            ));
            return;
        }

        if (newRank == GuildRole.LEADER) {
            Message.ERROR.send(player, String.format("A guilda pode ter apenas 1 %s.", GuildRole.LEADER.getDisplayName()));
            return;
        }

        targetRelation.setRole(newRank);

        GuildsProvider.Repositories.USERS_RELATIONS.provide().update(targetRelation);
        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(targetUser.getId());

        GuildsFrameworkProvider.Redis.ECHO.provide().publishToCurrentServer(
                new UserRankUpdatedPacket(targetUser.getId(), user.getId(), oldRank, newRank));

        Message.SUCCESS.send(player, String.format(
                "Você promoveu o jogador %s para %s.",
                targetUser.getName(),
                newRank.getDisplayName()
        ));
    }
}
