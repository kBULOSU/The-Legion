package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MenuIcon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class LeaveIcon extends MenuIcon {

    public LeaveIcon(User user, Supplier<CustomInventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(Material.DARK_OAK_DOOR)
                .name("&cSair da Guilda")
                .lore(
                        "&7Ao clicar neste ícone você",
                        "&7abandonará a sua guilda.",
                        "",
                        "&7Dica: Use /guilda sair",
                        "&cClique para sair."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            Bukkit.getPlayerExact(user.getName()).performCommand("guilda sair");
        };
    }

}
