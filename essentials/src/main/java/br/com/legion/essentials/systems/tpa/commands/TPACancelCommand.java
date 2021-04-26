package br.com.legion.essentials.systems.tpa.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.essentials.systems.tpa.TPASystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACancelCommand extends CustomCommand {

    public TPACancelCommand() {
        super("tpcancel", CommandRestriction.IN_GAME, "tpacancel", "tpacancelar");

        registerArgument(new NickArgument("jogador", "jogador que você deseja cacnelar o pedido de teleporte"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player requester = (Player) sender;

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            Message.ERROR.send(requester, String.format("O jogador %s não foi encontrado.", args[0]));
            return;
        }

        if (!target.isOnline()) {
            Message.ERROR.send(requester, String.format("O jogador %s não está online.", requester.getName()));
            return;
        }

        if (!TPASystem.hasRequest(requester.getName(), target.getName())) {
            Message.ERROR.send(sender, String.format("Você não enviou um pedido de teleporte para %s.", target.getName()));
            return;
        }

        TPASystem.remove(requester.getName(), target.getName());

        Message.INFO.send(target, "Pedido de teleporte cancelado por " + requester.getName() + ".");
        Message.INFO.send(sender, String.format("Pedido de teleporte para %s cancelado.", target.getName()));
    }
}
