package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.echo.packets.UserLeftGuildPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.server.ServerType;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

public class KickSubCommand extends GuildSubCommand {

    public KickSubCommand() {
        super("kick", GuildRole.CAPTAIN, "expulsar");

        registerArgument(new NickArgument("nick", "Nick do jogador que será expulso."));
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

        GuildUserRelation targetRelation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(targetUser.getId());

        if (targetRelation == null || targetRelation.getGuildId() != relation.getGuildId()) {
            Message.ERROR.send(player, String.format("O jogador %s não está na sua guilda.", targetNick));
            return;
        }

        if (relation.getRole().ordinal() <= targetRelation.getRole().ordinal()) {
            Message.ERROR.send(player, "Você não pode expulsar jogadores com o rank maior ou igual ao seu.");
            return;
        }

        GuildsProvider.Repositories.USERS_RELATIONS.provide().removeByUser(targetUser.getId());

        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(targetUser.getId());
        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateClan(targetRelation.getGuildId());

        GuildsFrameworkProvider.Redis.ECHO.provide().publishToCurrentServer(new UserLeftGuildPacket(
                targetRelation.getGuildId(),
                targetUser.getId(),
                UserLeftGuildPacket.Reason.KICK
        ));

        Message.SUCCESS.send(player, String.format("Você expulsou o jogador %s.", targetUser.getName()));
    }

}
