package br.com.legion.guilds.commands;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.messages.MessageUtils;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.echo.packets.SendMessagePacket;
import br.com.legion.guilds.relation.guild.GuildRelation;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

import java.util.Set;
import java.util.stream.IntStream;

public class AllyChatCommand extends CustomCommand {

    public AllyChatCommand() {
        super("a", CommandRestriction.IN_GAME);
        registerArgument(new Argument("mensagem", "Mensagem que será enviada para todos os aliados", true));
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(handle.getId());

        if (relation == null) {
            Message.ERROR.send(sender, "Você não está em uma guilda.");
            return;
        }

        Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());

        String messageRaw = String.join(" ", args);
        ComponentBuilder messageBuilder = new ComponentBuilder("");

        messageBuilder
                .append("[" + relation.getRole().getSymbol() + guild.getTag().toUpperCase() + "] ")
                .color(ChatColor.DARK_AQUA)
                .append(handle.getName() + ": ")
                .color(ChatColor.WHITE)
                .append(MessageUtils.stripColor('&', messageRaw))
                .color(ChatColor.DARK_AQUA);

        Set<GuildUserRelation> users = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByGuild(relation.getGuildId());

        Set<Integer> allies = GuildsProvider.Cache.Local.GUILDS_RELATIONS
                .provide().getRelationships(relation.getGuildId(), GuildRelation.ALLY);

        int[] usersIds = IntStream.concat(
                allies.stream()
                        .map(GuildsProvider.Cache.Local.USERS_RELATIONS.provide()::getByGuild)
                        .flatMap(Set::stream)
                        .mapToInt(GuildUserRelation::getUserId),
                users.stream().mapToInt(GuildUserRelation::getUserId)
        ).toArray();

        SendMessagePacket packet = new SendMessagePacket(
                usersIds,
                messageBuilder.create()
        );

        GuildsFrameworkProvider.Redis.ECHO.provide().publishToCurrentServer(packet);
    }
}
