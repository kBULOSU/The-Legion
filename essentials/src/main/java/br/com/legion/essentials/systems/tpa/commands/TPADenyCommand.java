package br.com.legion.essentials.systems.tpa.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.essentials.systems.tpa.TPASystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPADenyCommand extends CustomCommand {

    public TPADenyCommand() {
        super("tpdeny", CommandRestriction.IN_GAME, "tpanegar");

        registerArgument(new NickArgument("jogador", "jogador que você deseja aceitar o pedido de teleporte"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player target = (Player) sender;

        Player requester = Bukkit.getPlayerExact(args[0]);
        if (requester == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não foi encontrado.", args[0]));
            return;
        }

        if (!requester.isOnline()) {
            Message.ERROR.send(sender, String.format("O jogador %s não está online.", requester.getName()));
            return;
        }

        if (!TPASystem.hasRequest(target.getName(), requester.getName())) {
            Message.ERROR.send(sender, String.format("%s não te enviou um pedido de teleporte.", requester.getName()));
            return;
        }

        TPASystem.remove(target.getName(), requester.getName());

        Message.INFO.send(requester, "Pedido de teleporte para " + target.getName() + " negado.");
        Message.INFO.send(target, String.format("Você negou o pedido de teleporte de %s.", requester.getName()));
    }
}
