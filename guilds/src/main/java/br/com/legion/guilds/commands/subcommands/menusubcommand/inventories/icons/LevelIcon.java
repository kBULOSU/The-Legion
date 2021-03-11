package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MenuIcon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class LevelIcon extends MenuIcon {

    public LevelIcon(User user, Supplier<CustomInventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.DARK_OAK_DOOR)
                .name("&aLevel da Guilda")
                .lore(
                        "&7Veja informações sobre o level",
                        "&7da guilda e seus benefícios.",
                        "",
                        "&7Dica: Use /guilda level",
                        "&eClique para visualizar."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            Bukkit.getPlayerExact(user.getName()).performCommand("guilda level");
        };
    }
}
