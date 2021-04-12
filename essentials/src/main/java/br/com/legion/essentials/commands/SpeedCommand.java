package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.misc.utils.DefaultMessage;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand extends CustomCommand {

    public SpeedCommand() {
        super("speed", CommandRestriction.IN_GAME);

        registerArgument(new Argument("velocidade", "velocidade que será aplicada"));

        setPermission("command.speed");
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            resetPlayerSpeeds(player);
            Message.SUCCESS.send(player, "Suas velocidades foram resetadas.");
            return;
        }

        int speed = 0;
        boolean reset = false;
        Player target;

        if (args.length > 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                Message.ERROR.send(sender, DefaultMessage.PLAYER_NOT_FOUND.format(args[0]));
                return;
            }

            if (args[1].equalsIgnoreCase("reset")) {
                reset = true;
            } else {
                try {
                    speed = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    Message.ERROR.send(player, "Você especificou um número inválido: " + args[1] + ".");
                    return;
                }
            }
        } else {
            target = player;
            if (args[0].equalsIgnoreCase("reset")) {
                reset = true;
            } else {
                try {
                    speed = Integer.parseInt(args[0]);
                } catch (Exception e) {

                    Message.ERROR.send(player, "Você especificou um número inválido: " + args[0] + ".");
                    return;
                }
            }
        }

        if (reset) {
            resetPlayerSpeeds(target);
            Message.SUCCESS.send(sender, "As velocidades de " + target.getName() + " foram resetadas.");
            return;
        }

        speed = Math.max(0, Math.min(10, speed));

        float finalSpeed = (float) speed / 10F;

        if (target.isFlying()) {
            target.setFlySpeed(finalSpeed);
        } else {
            target.setWalkSpeed(finalSpeed);
        }

        String mode = (target.isFlying() ? "voo" : "corrida");

        if (target.getName().equals(player.getName())) {
            Message.SUCCESS.send(player, "Sua velocidade de " + mode + " foi alterada para: " + speed + ".");
        } else {
            Message.SUCCESS.send(sender, "A velocidade de " + mode + " de " + target.getName() + " foi alterada para: " + speed + ".");
        }

    }

    private void resetPlayerSpeeds(Player player) {
        player.setFlySpeed(0.1f);
        player.setWalkSpeed(0.2f);
    }
}
