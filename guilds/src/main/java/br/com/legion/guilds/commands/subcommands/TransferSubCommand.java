package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.server.ServerType;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

public class TransferSubCommand extends GuildSubCommand {

    public TransferSubCommand() {
        super("transferir", GuildRole.LEADER);

        registerArgument(new Argument("nick", "Nick do jogador que receberá a guilda"));
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
            Message.ERROR.send(player, "O jogador " + targetNick + " não foi encontrado.");
            return;
        }

        if (targetUser.equals(user)) {
            Message.ERROR.send(player, "Você precisa escolher outro jogador para transferir a guilda.");
            return;
        }

        targetNick = targetUser.getName();

        GuildUserRelation targetRelation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(targetUser.getId());
        Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());

        if (targetRelation == null || targetRelation.getGuildId() != relation.getGuildId()) {
            Message.ERROR.send(player, String.format(
                    "O jogador %s não está na guilda %s.",
                    targetNick,
                    guild.getTag().toUpperCase()
            ));

            return;
        }

        {
            targetRelation.setRole(GuildRole.LEADER);
            GuildsProvider.Repositories.USERS_RELATIONS.provide().update(targetRelation);
            GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(targetUser.getId());
        }

        {
            relation.setRole(GuildRole.CAPTAIN);
            GuildsProvider.Repositories.USERS_RELATIONS.provide().update(relation);
            GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(user.getId());
        }

        Message.SUCCESS.send(player, String.format(
                "Você transferiu a guilda %s para o jogador %s.",
                guild.getTag().toUpperCase(),
                targetUser.getName()
        ));

        ComponentBuilder builder = new ComponentBuilder("")
                .append(user.getName())
                .append(" transferiu a liderança da guilda para ", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN)
                .append(targetUser.getName())
                .append(".", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN);

        GuildUtils.broadcast(guild.getId(), builder.create());
    }
}
