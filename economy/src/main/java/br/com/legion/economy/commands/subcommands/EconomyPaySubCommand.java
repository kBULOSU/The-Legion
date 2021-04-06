package br.com.legion.economy.commands.subcommands;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.idea.api.spigot.misc.utils.DefaultMessage;
import br.com.legion.economy.EconomyAPI;
import com.google.common.primitives.Doubles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyPaySubCommand extends CustomCommand {

    public EconomyPaySubCommand() {
        super("pay", CommandRestriction.IN_GAME, "pagar", "enviar");

        this.registerArgument(new Argument("nick", "Nome do jogador."));
        this.registerArgument(new Argument("valor", ""));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        String targetName = args[0];
        if (player.getName().equalsIgnoreCase(targetName)) {
            Message.ERROR.send(sender, "\n &lBEEEEH! &cVocê não pode enviar coins para você mesmo.\n ");
            return;
        }

        User targetUser = ApiProvider.Cache.Local.USERS.provide().get(targetName);
        if (targetUser == null) {
            DefaultMessage.PLAYER_NOT_FOUND.send(sender, targetName);
            return;
        }

        Player targetPlayer = Bukkit.getPlayerExact(targetName);
        if (targetPlayer == null) {
            DefaultMessage.PLAYER_NOT_FOUND.send(sender, targetName);
            return;
        }

        Double value = Doubles.tryParse(args[1]);
        if (value == null || value.isNaN() || value.isInfinite() || value < 1.0D) {
            Message.ERROR.send(sender, "Quantia inválida.");
            return;
        }

        if (!EconomyAPI.has(player.getName(), value)) {
            Message.ERROR.send(player, "Você não possui essa quantia de coins.");
            return;
        }

        EconomyAPI.deposit(user.getId(), targetPlayer.getName(), value);
        EconomyAPI.withdraw(targetUser.getId(), player.getName(), value);

        player.sendMessage(String.format("§eVocê enviou §f%s coins §epara §f%s§e.", NumberUtils.format(value), targetPlayer.getName()));

        if (targetPlayer.isOnline()) {
            targetPlayer.sendMessage(String.format("§eVocê recebeu §f%s coins §ede §f%s§e.", NumberUtils.format(value), player.getName()));
        }
    }
}

