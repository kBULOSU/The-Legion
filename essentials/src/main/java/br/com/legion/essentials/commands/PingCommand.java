package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand extends CustomCommand {

    public PingCommand() {
        super("ping", CommandRestriction.IN_GAME);

        registerArgument(new Argument("nome", "de outro alguém", false));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            Message.SUCCESS.send(
                    player,
                    String.format(
                            "Seu ping é de %s ms.",
                            ((CraftPlayer) player).getHandle().ping
                    )
            );
        } else {

            Player playerExact = Bukkit.getPlayerExact(args[0]);

            if (playerExact == null) {
                Message.ERROR.send(player, "Usuário inválido.");
                return;
            }

            Message.SUCCESS.send(
                    player,
                    String.format(
                            "O ping de " + playerExact.getName() + " é de %s ms.",
                            ((CraftPlayer) playerExact).getHandle().ping
                    )
            );
        }
    }
}
