package br.com.legion.essentials.systems.tpa.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.essentials.misc.utils.TextComponentUtils;
import br.com.legion.essentials.systems.tpa.TPASystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACommand extends CustomCommand {

    public TPACommand() {
        super("tpa", CommandRestriction.IN_GAME);

        registerArgument(new NickArgument("nick", "jogador que você deseja enviar o pedido de teleporte"));
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
            Message.ERROR.send(requester, String.format("O jogador %s não está online.", target.getName()));
            return;
        }

        if (TPASystem.hasRequest(requester.getName(), target.getName())) {
            Message.ERROR.send(sender, String.format("Você já possui um pedido de teleporte pendente para o jogador %s.", target.getName()));
            return;
        }

        if (target.equals(requester)) {
            Message.ERROR.send(sender, "Você já está onde você está.");
            return;
        }

        TPASystem.request(target.getName(), requester.getName());

        target.sendMessage(new String[]{
                "",
                "§e* " + requester.getName() + " lhe enviou um pedido de teletransporte!",
                ""
        });
        target.sendMessage(TextComponentUtils.getAcceptOrDenyMessage(
                ChatColor.YELLOW, "/tpaccept " + requester.getName(), "/tpdeny " + requester.getName())
        );

        ComponentBuilder builder = new ComponentBuilder("\n")
                .append("Você enviou um pedido de teleporte para " + target.getName()).color(ChatColor.YELLOW)
                .append("\n")
                .append("Clique").color(ChatColor.YELLOW)
                .append(" AQUI").color(ChatColor.RED).bold(true)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpcancel " + target.getName()))
                .append(" para cancelar.").color(ChatColor.YELLOW).bold(false)
                .append("\n");

        ((Player) sender).sendMessage(builder.create());
    }
}
