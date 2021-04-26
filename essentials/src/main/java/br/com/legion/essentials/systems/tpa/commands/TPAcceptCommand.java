package br.com.legion.essentials.systems.tpa.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.idea.api.spigot.misc.utils.EntityUtils;
import br.com.legion.essentials.systems.tpa.TPASystem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAcceptCommand extends CustomCommand {

    public TPAcceptCommand() {
        super("tpaccept", CommandRestriction.IN_GAME, "tpaaceitar");

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
            Message.ERROR.send(sender, "Você não possui um pedido de teleporte deste jogador.");
            return;
        }

        if (EntityUtils.isStuck(target)) {
            Message.ERROR.send(target, "Você não pode aceitar TPA estando sobterrado.");
            return;
        }

        TPASystem.remove(target.getName(), requester.getName());

        requester.teleport(target);

        Message.SUCCESS.send(target, String.format("Você aceitou o pedido de %s com sucesso.", requester.getName()));
        Message.SUCCESS.send(requester, String.format("&aPedido de teleporte aceito por %s.", target.getName()));
    }
}
