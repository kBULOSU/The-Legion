package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public abstract class MenuIcon {

    public final User user;
    public final Supplier<CustomInventory> back;

    public abstract ItemStack getIcon();

    public abstract Runnable getRunnable();
}
