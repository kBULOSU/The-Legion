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

public class TPCommand extends CustomCommand {

    public TPCommand() {
        super("tp", CommandRestriction.IN_GAME);

        registerArgument(new NickArgument("nick", "Nick do jogador que será procurado", true));

        setPermission("command.tp");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        User targetUser = ApiProvider.Cache.Local.USERS.provide().get(args[0]);

        if (targetUser == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não foi encontrado.", args[0]));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não está online.", targetUser.getName()));
            return;
        }

        ((Player) sender).teleport(target);

        Message.SUCCESS.send(sender, "Teleportado com sucesso.");
    }
}
