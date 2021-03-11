package br.com.legion.guilds.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.messages.MessageUtils;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.echo.packets.SendMessagePacket;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class GuildChatCommand extends CustomCommand {

    public GuildChatCommand() {
        super("c", CommandRestriction.IN_GAME, ".");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user.getId());

        if (relation == null) {
            Message.ERROR.send(sender, "Você não está em uma guilda.");
            return;
        }

        String messageRaw = String.join(" ", args);
        ComponentBuilder messageBuilder = new ComponentBuilder("");

        messageBuilder
                .append(relation.getRole().getSymbol())
                .color(ChatColor.GREEN)
                .append(user.getName() + ": ")
                .color(ChatColor.WHITE)
                .append(MessageUtils.stripColor('&', messageRaw))
                .color(ChatColor.AQUA);

        Set<GuildUserRelation> users = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByGuild(relation.getGuildId());
        SendMessagePacket packet = new SendMessagePacket(
                users.stream().mapToInt(GuildUserRelation::getUserId).toArray(),
                messageBuilder.create()
        );

        GuildsFrameworkProvider.Redis.ECHO.provide().publishToAll(packet);
    }
}
