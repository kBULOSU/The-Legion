package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Set;

public class InfoSubCommand extends GuildSubCommand {

    public InfoSubCommand() {
        super("info");

        registerArgument(new Argument("tag", "A tag da guilda", false));
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {

        Guild guild;

        if (args.length > 0) {
            String tag = args[0];
            if (!GuildsConstants.TAG_PATTERN.matcher(tag).matches()) {
                Message.ERROR.send(player, "A tag da guilda não pode conter caracteres especiais.");
                return;
            }

            guild = GuildsProvider.Cache.Local.GUILDS.provide().getByTag(tag);

            if (guild == null) {
                Message.ERROR.send(player, String.format("A guilda '%s' não existe.", tag));
                return;
            }
        } else {
            if (relation == null) {
                Message.ERROR.send(player, "Use /guilda info <tag>");
                return;
            }

            guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());
        }

        ComponentBuilder builder = new ComponentBuilder(String.format("%s - %s", guild.getTag().toUpperCase(), guild.getName()))
                .color(ChatColor.YELLOW)
                .append("\n")
                .append("§eMembros: ")
                .append(String.valueOf(
                        GuildUtils.getUsers(guild.getId(), false, false).size()
                ))
                .color(ChatColor.GRAY)
                .append("/").append(String.valueOf(guild.getMaxMembers()))
                .color(ChatColor.GRAY)
                .append("\n")
                .append("Membros online: ")
                .color(ChatColor.YELLOW);

        Set<User> onlineUsers = GuildUtils.getUsers(guild.getId(), false, true);
        if (onlineUsers.isEmpty()) {
            builder.append("Ninguém está online.");
        } else {
            Iterator<User> iterator = onlineUsers.iterator();

            while (iterator.hasNext()) {
                builder.append(iterator.next().getName());

                if (iterator.hasNext()) {
                    builder.append(", ", ComponentBuilder.FormatRetention.NONE);
                } else {
                    builder.append(".", ComponentBuilder.FormatRetention.NONE);
                }

                builder.color(ChatColor.GRAY);
            }
        }

        builder.color(ChatColor.GRAY)
                .append("\n");

        player.spigot().sendMessage(builder.create());
    }
}
