package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.user.User;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.inventory.GuildsListInventory;
import org.bukkit.entity.Player;

public class ListSubCommand extends GuildSubCommand {

    public ListSubCommand() {
        super("list", "listar");
    }

    @Override
    public void onCommand(Player player, User user, String[] args) {
        player.openInventory(
                new GuildsListInventory()
        );
    }
}
