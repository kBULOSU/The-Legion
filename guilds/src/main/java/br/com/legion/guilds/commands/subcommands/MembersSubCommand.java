package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.misc.utils.Plural;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MembersSubCommand extends GuildSubCommand {

    public static final LoadingCache<Integer, Set<User>> ALL_USERS_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(GuildUtils::getUsers);

    public static final LoadingCache<Integer, Set<User>> ONLINE_USERS_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(target -> {
                return GuildUtils.getUsers(target, false, true);
            });

    public MembersSubCommand() {
        super("membros");

        this.setUsage("");

        registerArgument(new Argument("tag", "Tag da guilda", false));
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {

        Guild guild = null;

        if (relation != null) {
            guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());
        }

        if (args.length < 1 && relation == null) {
            player.sendMessage(getUsage(player, "membros"));
            return;
        }

        if (args.length > 0) {
            String tag = args[0].toUpperCase();

            guild = GuildsProvider.Cache.Local.GUILDS.provide().getByTag(tag);

            if (guild == null) {
                Message.ERROR.send(player, String.format("Não existe uma guilda com a tag '%s'.", tag));
                return;
            }
        }

        Set<User> allUsers = ALL_USERS_CACHE.get(guild.getId());
        Set<User> onlineUsers = ONLINE_USERS_CACHE.get(guild.getId());

        Set<GuildUserRelation> relations = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByGuild(guild.getId());
        Map<Integer, GuildRole> rolesMap = relations.stream().collect(Collectors.toMap(
                GuildUserRelation::getUserId, GuildUserRelation::getRole));

        ComponentBuilder builder = new ComponentBuilder("\n");

        allUsers.stream()
                .collect(Collectors.groupingBy(target -> rolesMap.get(target.getId())))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getKey().compareTo(o1.getKey()))
                .forEach(entry -> {
                    GuildRole role = entry.getKey();
                    Collection<User> users = entry.getValue();

                    String roleDisplayName = Plural.of(users.size(), role.getDisplayName(), role.getDisplayPluralName());

                    builder.reset()
                            .append(role.getSymbol())
                            .color(ChatColor.YELLOW)
                            .append(roleDisplayName)
                            .append(": ");

                    Set<BaseComponent[]> components = users.stream()
                            .map(targetUser -> {

                                boolean isLogged = onlineUsers.contains(targetUser);

                                ComponentBuilder targetBuilder = new ComponentBuilder("\u25CF")
                                        .color(isLogged ? ChatColor.GREEN : ChatColor.RED)
                                        .append(" ")
                                        .append(targetUser.getName());

                                return targetBuilder.create();
                            })
                            .collect(Collectors.toSet());

                    Iterator<BaseComponent[]> iterator = components.iterator();

                    while (iterator.hasNext()) {
                        BaseComponent[] bcs = iterator.next();

                        builder.append(bcs, ComponentBuilder.FormatRetention.NONE)
                                .color(ChatColor.GRAY);

                        if (iterator.hasNext()) {
                            builder.append(", ");
                        } else {
                            builder.append(".");
                        }
                    }

                    builder.append("\n");
                });

        player.spigot().sendMessage(builder.create());
    }
}
