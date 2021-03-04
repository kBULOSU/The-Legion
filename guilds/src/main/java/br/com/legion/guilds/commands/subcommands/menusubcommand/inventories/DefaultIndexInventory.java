package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons.GuildInformationIcon;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons.HelpIcon;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Supplier;

public class DefaultIndexInventory extends CustomInventory {

    public DefaultIndexInventory(User user) {
        super(3 * 9, "Menu de Guildas");

        Map<Integer, MenuIcon> icons = Maps.newHashMap();

        Supplier<CustomInventory> supplier = () -> new DefaultIndexInventory(user);

        icons.put(12, new GuildInformationIcon(user, supplier));
        icons.put(14, new HelpIcon(user, supplier));

        icons.forEach((slot, icon) -> {
            setItem(slot, icon.getIcon(), icon.getRunnable());
        });
    }
}
