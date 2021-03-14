package br.com.legion.guilds.misc.utils;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.utils.BannerAlphabet;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.echo.packets.SendMessagePacket;
import br.com.legion.guilds.relation.guild.GuildRelation;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GuildUtils {

    public static void broadcast(int guildId, String message, boolean local, GuildRole... roles) {
        broadcast(guildId, TextComponent.fromLegacyText(message), local, roles);
    }

    public static void broadcast(int guildId, BaseComponent[] components, GuildRole... roles) {
        broadcast(guildId, components, false, roles);
    }

    public static void broadcast(int guildId, BaseComponent[] components, boolean local, GuildRole... roles) {
        if (local) {
            getUsers(guildId, true, roles).stream()
                    .map(user -> Bukkit.getPlayerExact(user.getName()))
                    .filter(Objects::nonNull)
                    .filter(Player::isOnline)
                    .forEach(player -> {
                        player.spigot().sendMessage(components);
                    });
        } else {
            GuildsFrameworkProvider.Redis.ECHO.provide().publishToAll(new SendMessagePacket(
                    getUsers(guildId).stream().mapToInt(User::getId).toArray(),
                    components
            ));
        }
    }

    /*

     */

    public static Set<User> getUsers(int guildId, GuildRole... roles) {
        return getUsers(guildId, false, roles);
    }

    public static Set<User> getUsers(int guildId, boolean local, GuildRole... roles) {
        return getUsers(guildId, local, false, roles);
    }

    public static Set<User> getUsers(int guildId, boolean local, boolean onlineOnly, GuildRole... roles) {
        Set<GuildUserRelation> relations = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByGuild(guildId);

        if (roles == null || roles.length == 0) {
            roles = GuildRole.values();
        }

        List<GuildRole> roleList = Arrays.asList(roles);

        relations = relations.stream()
                .filter(relation -> roleList.contains(relation.getRole()))
                .collect(Collectors.toSet());

        Stream<User> out = relations.stream()
                .map(relation -> ApiProvider.Cache.Local.USERS.provide().get(relation.getUserId()))
                .filter(Objects::nonNull);

        if (local) {
            return out.filter(user -> {
                Player player = Bukkit.getPlayerExact(user.getName());
                return player != null && player.isOnline();
            }).collect(Collectors.toSet());
        } else if (!onlineOnly) {
            return out.collect(Collectors.toSet());
        }

        return out.map(User::getName)
                .map(name -> ApiProvider.Cache.Local.USERS.provide().get(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /*

     */

    public static ItemBuilder getBanner(@NonNull Guild guild) {
        return getBanner(guild, (Guild) null);
    }

    public static ItemBuilder getBanner(@NonNull Guild guild, User user) {
        if (user != null) {
            GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user.getId());

            if (relation != null) {
                Guild guild2 = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());
                return getBanner(guild, guild2);
            }
        }

        return getBanner(guild, (Guild) null);
    }

    public static ItemBuilder getBanner(@NonNull Guild guild, Guild other) {
        ItemBuilder builder = new ItemBuilder(Material.BANNER)
                .flags(ItemFlag.HIDE_POTION_EFFECTS);

        BannerAlphabet bannerAlphabet = BannerAlphabet.getBanner(guild.getTag().charAt(0));

        DyeColor backgroundColor = DyeColor.SILVER;
        String colorCode = "&7";

        if (bannerAlphabet != null) {

            if (other != null && Objects.equals(guild, other)) {
                backgroundColor = DyeColor.ORANGE;
                colorCode = "&6";
            } else if (other != null && GuildsProvider.Cache.Local.GUILDS_RELATIONS.provide().getRelation(guild.getId(), other.getId()) == GuildRelation.ALLY) {
                backgroundColor = DyeColor.LIME;
                colorCode = "&a";
            }

            builder.patterns(bannerAlphabet.buildPatterns(backgroundColor, DyeColor.BLACK));
        }

        builder.color(backgroundColor);

        builder.name(String.format("%s[%s] %s", colorCode, guild.getTag(), guild.getName()));

        return builder;
    }

}
