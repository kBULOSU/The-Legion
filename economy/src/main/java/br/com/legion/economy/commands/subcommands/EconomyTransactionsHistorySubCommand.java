package br.com.legion.economy.commands.subcommands;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.economy.inventories.TransactionsInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyTransactionsHistorySubCommand extends CustomCommand {

    public EconomyTransactionsHistorySubCommand() {
        super("historico", CommandRestriction.IN_GAME, "history");

        registerArgument(new Argument("nome", "", false));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        String target = sender.getName();

        if (args.length == 1) {
            target = args[0];
        }

        if (!sender.hasPermission("economy.admin") && !target.equals(sender.getName())) {
            Message.ERROR.send(sender, "Você não tem permissão para visualizar extratos de outros jogadores.");
            return;
        }

        User user = ApiProvider.Cache.Local.USERS.provide().get(target);
        if (user == null) {
            Message.ERROR.send(sender, "Usuário inválido.");
            return;
        }

        ((Player) sender).openInventory(new TransactionsInventory(user));
    }
}
