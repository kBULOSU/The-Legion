package br.com.legion.guilds.commands.subcommands.menusubcommand;

import br.com.idea.api.shared.user.User;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.DefaultIndexInventory;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.LeaderIndexInventory;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MemberIndexInventory;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

public class MenuSubCommand extends GuildSubCommand {

    public MenuSubCommand() {
        super("menu", "gui");
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        if (relation == null) {
            player.openInventory(new DefaultIndexInventory(user));
            return;
        }

        if (relation.getRole() == GuildRole.LEADER) {
            player.openInventory(new LeaderIndexInventory(user));
            return;
        }

        player.openInventory(new MemberIndexInventory(user));
    }
}
