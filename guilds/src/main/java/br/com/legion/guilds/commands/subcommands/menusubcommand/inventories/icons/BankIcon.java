package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.HeadTexture;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MenuIcon;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class BankIcon extends MenuIcon {

    public BankIcon(User user, Supplier<CustomInventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = new ItemBuilder(HeadTexture.getTempHead(GuildsConstants.MONEY_HEAD_KEY))
                .name("&aBanco da Guilda")
                .lore(
                        "&7Gerencie o banco da guilda.",
                        "",
                        "&7Dica: Use /guilda banco",
                        "&eClique para visualizar."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            Bukkit.getPlayerExact(user.getName()).performCommand("guilda banco");
        };
    }
}
