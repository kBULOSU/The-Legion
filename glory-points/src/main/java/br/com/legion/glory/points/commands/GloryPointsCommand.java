package br.com.legion.glory.points.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.misc.utils.DefaultMessage;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.glory.points.GloryPointsAPI;
import br.com.legion.glory.points.commands.subcommands.GloryPointsAddSubCommand;
import br.com.legion.glory.points.commands.subcommands.GloryPointsDefineSubCommand;
import br.com.legion.glory.points.commands.subcommands.GloryPointsRemoveSubCommand;
import br.com.legion.glory.points.commands.subcommands.GloryPointsTopSubCommand;
import br.com.legion.glory.points.inventories.IndexInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GloryPointsCommand extends CustomCommand {

    public GloryPointsCommand() {
        super("glorypoints", CommandRestriction.CONSOLE_AND_IN_GAME, "gp", "points");

        registerSubCommand(new GloryPointsAddSubCommand());
        registerSubCommand(new GloryPointsRemoveSubCommand());
        registerSubCommand(new GloryPointsDefineSubCommand());
        registerSubCommand(new GloryPointsTopSubCommand());
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

            Double balance = GloryPointsAPI.get(offlinePlayer.getName());
            if (balance.isNaN() || balance.isInfinite()) {
                Message.ERROR.send(player, "Os pontos de glória deste jogador são inválidos.");
                return;
            }

            player.sendMessage("§e" + offlinePlayer.getName() + " possui: §f" + NumberUtils.format(balance) + " §epontos de glória.");
        }
    }
}
