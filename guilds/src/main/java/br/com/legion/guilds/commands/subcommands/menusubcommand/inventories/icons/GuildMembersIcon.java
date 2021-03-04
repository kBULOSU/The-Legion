package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.HeadTexture;
import br.com.idea.api.spigot.misc.utils.InventoryUtils;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.subcommands.MembersSubCommand;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MenuIcon;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GuildMembersIcon extends MenuIcon {

    public GuildMembersIcon(User user, Supplier<CustomInventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.SKULL_ITEM)
                .durability(3)
                .name("&aMembros")
                .lore(
                        "&7Veja informações sobre cada",
                        "&7membros da sua guilda!",
                        "",
                        "&7Dica: use /guilda membros",
                        "&eClique para visualizar."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user.getId());
            Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());

            Bukkit.getPlayerExact(user.getName()).openInventory(new Inventory(guild));
        };
    }

    private final class Inventory extends CustomInventory {

        public Inventory(Guild guild) {
            super(54, "Membros - [" + guild.getTag() + "]");

            Set<User> allUsers = MembersSubCommand.ALL_USERS_CACHE.get(guild.getId());
            Set<User> onlineUsers = MembersSubCommand.ONLINE_USERS_CACHE.get(guild.getId());

            Set<GuildUserRelation> relations = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByGuild(guild.getId());
            Map<Integer, GuildRole> rolesMap = relations.stream()
                    .collect(Collectors.toMap(GuildUserRelation::getUserId, GuildUserRelation::getRole));

            Iterator<User> allUsersIterator = allUsers.stream()
                    .collect(Collectors.groupingBy(target -> rolesMap.get(target.getId())))
                    .entrySet()
                    .stream()
                    .sorted((o1, o2) -> o2.getKey().compareTo(o1.getKey()))
                    .flatMap(entry -> entry.getValue().stream())
                    .collect(Collectors.toCollection(LinkedHashSet::new))
                    .iterator();

            int slot = 11;
            for (int i = 0; i < guild.getMaxMembers(); i++) {

                if (!allUsersIterator.hasNext()) {
                    this.setItem(slot++, ItemBuilder.of(Material.SKULL_ITEM).name("&7Vago").make());
                } else {
                    User user = allUsersIterator.next();
                    boolean isLogged = onlineUsers.contains(user);

                    String displayName = (isLogged ? "&a" : "&c") + "\u25CF " + user.getName();

                    this.setItem(slot++, new ItemBuilder(HeadTexture.getPlayerHead(user.getName()))
                            .name(displayName)
                            .make()
                    );
                }

                if (InventoryUtils.getColumn(slot) == 7) {
                    slot += 4;
                }
            }

            setItem(49, BACK_ARROW, () -> {
                Bukkit.getPlayerExact(user.getName()).openInventory(GuildMembersIcon.this.getBack().get());
            });
        }

    }

}
