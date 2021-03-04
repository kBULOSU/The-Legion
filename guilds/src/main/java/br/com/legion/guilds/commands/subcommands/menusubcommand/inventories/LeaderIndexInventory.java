package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons.*;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Supplier;

public class LeaderIndexInventory extends CustomInventory {

    public LeaderIndexInventory(User user) {
        super(4 * 9, "Menu de Guildas");

        Map<Integer, MenuIcon> icons = Maps.newHashMap();

        Supplier<CustomInventory> supplier = () -> new LeaderIndexInventory(user);

        icons.put(10, new HelpIcon(user, supplier));
        icons.put(16, new GuildInformationIcon(user, supplier));
        icons.put(20, new AlliesIcon(user, supplier));
        icons.put(21, new AllyInvitationsIcon(user, supplier));
        icons.put(22, new BankIcon(user, supplier));
        icons.put(23, new GuildInvitationsIcon(user, supplier));
        icons.put(24, new GuildMembersIcon(user, supplier));
        icons.put(25, new DisbandIcon(user, supplier));

        icons.forEach((slot, icon) -> {
            setItem(slot, icon.getIcon(), icon.getRunnable());
        });
    }

}
