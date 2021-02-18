package br.com.legion.guilds.commands;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GuildSubCommand extends CustomCommand {

    private final GuildRole rank;

    public GuildSubCommand(String name, String... aliases) {
        this(name, null, aliases);
    }

    public GuildSubCommand(String name, GuildRole role, String... aliases) {
        super(name, CommandRestriction.IN_GAME, aliases);

        this.rank = role;
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(handle.getId());

        if (rank != null) {
            if (relation == null) {
                Message.ERROR.send(sender, "Você precisa fazer parte de uma guilda.");
                return;
            }

            if (rank.ordinal() > relation.getRole().ordinal()) {
                Message.ERROR.send(sender, String.format("Você precisa ter o rank %s.", rank.getDisplayName()));
                return;
            }
        }

        onCommand((Player) sender, handle, relation, args);
    }

    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        onCommand(player, user, args);
    }

    public void onCommand(Player player, User user, String[] args) {
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player) || args.length <= 0) {
            return ImmutableList.of();
        }

        User user = ApiProvider.Cache.Local.USERS.provide().get(sender.getName());
        GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user.getId());

        if (relation != null) {

            if (this.getArguments().size() >= args.length) {
                int index = args.length - 1;
                String token = args[index];

                if (!token.isEmpty()) {
                    Argument argument = this.getArguments().get(index);

                    if (argument instanceof NickArgument) {

                        Set<GuildUserRelation> relations = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByGuild(relation.getGuildId());

                        Set<String> nicks = relations.stream()
                                .map(_rel -> ApiProvider.Cache.Local.USERS.provide().get(_rel.getUserId()))
                                .map(User::getName)
                                .filter(nick -> StringUtil.startsWithIgnoreCase(nick, args[index]))
                                .collect(Collectors.toSet());

                        return StringUtil.copyPartialMatches(args[index], nicks, new ArrayList(nicks.size()));
                    }
                }
            }
        }

        return ImmutableList.of();
    }
}
