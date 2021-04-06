package br.com.legion.economy.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.misc.utils.DefaultMessage;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.economy.EconomyAPI;
import br.com.legion.economy.commands.subcommands.*;
import br.com.legion.economy.inventories.IndexInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommand extends CustomCommand {

    public EconomyCommand() {
        super("coins", CommandRestriction.CONSOLE_AND_IN_GAME, "money", "dinheiro");

        registerSubCommand(new EconomyAddSubCommand());
        registerSubCommand(new EconomyRemoveSubCommand());
        registerSubCommand(new EconomyDefineSubCommand());
        registerSubCommand(new EconomyAddSubCommand());
        registerSubCommand(new EconomyTransactionsHistorySubCommand());
        registerSubCommand(new EconomyPaySubCommand());
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                return;
            }

            Player player = (Player) sender;

           /*
           Double balance = GloryPointsAPI.get(player.getName());
            if (balance.isNaN() || balance.isInfinite()) {
                Message.ERROR.send(player, "Seus pontos de glória estão inválidos, chame um membro da equipe urgente!");
                return;
            }

            player.sendMessage("§eVocê possui: §f" + NumberUtils.format(balance) + " §epontos de glória.");
            */

            player.openInventory(new IndexInventory(user));
        } else if (args.length == 1) {
            if (!(sender instanceof Player)) {
                return;
            }

            Player player = (Player) sender;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            if (offlinePlayer == null) {
                Message.ERROR.send(player, DefaultMessage.PLAYER_NOT_FOUND.getDefaultRawMessage());
                return;
            }

            Double balance = EconomyAPI.get(offlinePlayer.getName());
            if (balance.isNaN() || balance.isInfinite()) {
                Message.ERROR.send(player, "Os coins deste jogador são inválidos.");
                return;
            }

            player.sendMessage("§2" + offlinePlayer.getName() + " possui: §f" + NumberUtils.format(balance) + " §2coins.");
        }
    }
}
