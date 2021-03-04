package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MenuIcon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class AlliesIcon extends MenuIcon {

    public AlliesIcon(User user, Supplier<CustomInventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.BOOK)
                .name("&aGuildas Aliadas")
                .lore(
                        "&7Veja e gerencie as alianças",
                        "&7que a sua guilda possui!",
                        "",
                        "&7Dica: Use /guilda relacao",
                        "&eClique para visualizar."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            Bukkit.getPlayerExact(user.getName()).performCommand("guilda relacao");
        };
    }

}
