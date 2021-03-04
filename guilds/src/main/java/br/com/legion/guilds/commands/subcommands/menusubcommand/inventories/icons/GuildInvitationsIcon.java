package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MenuIcon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.Supplier;

public class GuildInvitationsIcon extends MenuIcon {

    public GuildInvitationsIcon(User user, Supplier<CustomInventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.IRON_HELMET)
                .name("&aConvites de Guildas")
                .lore(
                        "&7Veja as guildas que você está",
                        "&7sendo convidado a participar!",
                        ""
                );

        Set<Integer> invites = GuildsProvider.Cache.Redis.GUILD_INVITATIONS.provide().getInvitations(user.getId());

        if (invites.isEmpty()) {
            builder.lore("&fConvites pendentes: &c0");
        } else {
            builder.lore("&fConvites pendentes: &a" + invites.size());
        }

        builder.lore(
                "",
                "&7Dica: Use /guilda aceitar",
                "&eClique para visualizar."
        );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            Bukkit.getPlayerExact(user.getName()).performCommand("guilda aceitar");
        };
    }

}
