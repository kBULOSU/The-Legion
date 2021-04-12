package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.misc.utils.DefaultMessage;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.idea.api.spigot.misc.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearCommand extends CustomCommand {

    public ClearCommand() {
        super("clear", CommandRestriction.IN_GAME);

        setPermission("command.clear");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player target = null;

        if (sender instanceof Player) {
            target = (Player) sender;
        }

        if (!(sender instanceof Player) && args.length == 0) {
            Message.ERROR.send(sender, "Utilize /clear <nick>.");
            return;
        }

        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
        }

        if (target == null) {
            Message.ERROR.send(sender, DefaultMessage.PLAYER_NOT_FOUND.format(args[0]));
            return;
        }

        if (!sender.hasPermission("command.clear.others")) {
            target = (Player) sender;
        }

        PlayerUtils.clear(target);

        if (((Player) sender) != target) {
            Message.SUCCESS.send(sender, "Você limpou o inventário de " + target.getName() + "!");
            return;
        }

        Message.SUCCESS.send(sender, "Seu inventário agora está vazio.");
    }
}
