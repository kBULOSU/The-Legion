package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.HeadTexture;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MenuIcon;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class HelpIcon extends MenuIcon {

    public HelpIcon(User user, Supplier<CustomInventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder builder = ItemBuilder.of(HeadTexture.getTempHead("9e5bb8b31f46aa9af1baa88b74f0ff383518cd23faac52a3acb96cfe91e22ebc"))
                .name("&eAjuda")
                .lore(
                        "&7Está com dúvidas sobre o sistema",
                        "&7de guildas? Veja dicas sobre o",
                        "&7que cada comando faz!",
                        "",
                        "&7Dica: Use /guilda",
                        "&eClique para visualizar."
                );

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
            Bukkit.getPlayerExact(user.getName()).performCommand("guilda");
            Bukkit.getPlayerExact(user.getName()).closeInventory();
        };
    }

}
