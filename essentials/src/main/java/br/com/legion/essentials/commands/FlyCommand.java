package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends CustomCommand {

    public FlyCommand() {
        super("fly", CommandRestriction.IN_GAME);

        setPermission("command.fly");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;
        Player targetPlayer = null;

        if (args.length > 0) {

            targetPlayer = Bukkit.getPlayerExact(args[0]);

            if (targetPlayer == null) {
                Message.ERROR.send(sender, String.format("O jogador \"%s\" não está online.", args[0]));
                return;
            }
        }

        if (targetPlayer != null && player != targetPlayer) {
            targetPlayer.setAllowFlight(!targetPlayer.getAllowFlight());
            targetPlayer.setFlying(targetPlayer.getAllowFlight());

            String string = String.format(targetPlayer.getAllowFlight() ? "Modo voo de \"%s\" ativo." : "Modo voo de \"%s\" desativado.", targetPlayer.getName());
            Message.SUCCESS.send(sender, string);
            return;
        }

        player.setAllowFlight(!player.getAllowFlight());
        player.setFlying(player.getAllowFlight());
        Message.SUCCESS.send(sender, player.getAllowFlight() ? "Modo voo ativo." : "Modo voo desativado.");
    }
}
