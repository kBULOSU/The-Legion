package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.echo.packets.UserInvitedGuildPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.server.ServerType;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

import java.util.Set;

public class InviteSubCommand extends GuildSubCommand {

    public InviteSubCommand() {
        super("convidar", GuildRole.CAPTAIN, "invite");

        registerArgument(new NickArgument("nick", "Nick do jogador que será convidado"));
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        Set<User> allUsers = GuildUtils.getUsers(relation.getGuildId());
        Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());

        if (allUsers.size() >= guild.getMaxMembers()) {
            Message.ERROR.send(player, String.format(
                    "A guilda já atingiu o número máximo de %s jogadores.",
                    guild.getMaxMembers()
            ));

            return;
        }

        String targetNick = args[0];

        User targetUser = ApiProvider.Cache.Local.USERS.provide().get(targetNick);

        if (targetUser == null) {
            Message.ERROR.send(player, "O jogador " + targetNick + " não foi encontrado.");
            return;
        }

        if (targetUser.equals(user)) {
            Message.ERROR.send(player, "Você precisa escolher outro jogador para convidar.");
            return;
        }

        targetNick = targetUser.getName();

        GuildUserRelation targetRelation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(targetUser.getId());
        if (targetRelation != null) {
            Message.ERROR.send(player, "O jogador " + targetNick + " já está em uma guilda.");
            return;
        }

        if (GuildsProvider.Cache.Redis.GUILD_INVITATIONS.provide().hasInvite(targetUser.getId(), relation.getGuildId())) {
            Message.ERROR.send(player, "O jogador " + targetNick + " já foi convidado para sua guilda.");
            return;
        }

        GuildsProvider.Cache.Redis.GUILD_INVITATIONS.provide().putInvitation(targetUser.getId(), relation.getGuildId());

        GuildsFrameworkProvider.Redis.ECHO.provide()
                .publishToCurrentServer(new UserInvitedGuildPacket(relation.getGuildId(), targetUser.getId(), user.getId()));

        Message.SUCCESS.send(player, "Você convidou o jogador " + targetNick + " para sua guilda.");
    }
}
