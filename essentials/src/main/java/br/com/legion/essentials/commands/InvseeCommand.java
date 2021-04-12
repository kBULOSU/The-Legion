package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.misc.utils.DefaultMessage;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.inventory.CustomPlayerInventory;
import br.com.idea.api.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand extends CustomCommand {

    public InvseeCommand() {
        super("invsee", CommandRestriction.IN_GAME);

        setPermission("command.invsee");
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        Player player = (Player) sender;

        String targetRaw = args[0];

        Player target = Bukkit.getPlayerExact(targetRaw);

        if (target == player) {
            Message.ERROR.send(sender, "Você não pode usar esse comando em você mesmo.");
            return;
        }

        if (target != null) {
            player.closeInventory();
            CustomPlayerInventory inventory = new CustomPlayerInventory(target, true);
            player.openInventory(inventory.getBukkitInventory());
            return;
        }

        Message.ERROR.send(sender, DefaultMessage.PLAYER_NOT_FOUND.format(targetRaw));

    }
}
