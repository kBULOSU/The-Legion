package br.com.legion.essentials.commands;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPHereCommand extends CustomCommand {

    public TPHereCommand() {
        super("tphere", CommandRestriction.IN_GAME);

        registerArgument(new NickArgument("nick", "Nick do jogador", true));

        setPermission("command.tphere");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        User targetUser = ApiProvider.Cache.Local.USERS.provide().get(args[0]);

        if (targetUser == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não foi encontrado.", args[0]));
            return;
        }

        Player target = Bukkit.getPlayer(targetUser.getName());
        if (target == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não está online.", targetUser.getName()));
            return;
        }

        target.teleport(((Player) sender));

        Message.SUCCESS.send(target, "Teleportado com sucesso.");
    }
}
