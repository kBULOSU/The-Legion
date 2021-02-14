package br.com.legion.glory.points.commands.subcommands;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.commands.arguments.NickArgument;
import br.com.idea.api.shared.misc.utils.DefaultMessage;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.glory.points.GloryPointsAPI;
import com.google.common.primitives.Doubles;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class GloryPointsRemoveSubCommand extends CustomCommand {

    public GloryPointsRemoveSubCommand() {
        super("remove", CommandRestriction.CONSOLE_AND_IN_GAME, "remover");

        registerArgument(new NickArgument("nome", "Nome do destinatário."));
        registerArgument(new Argument("quantia", "Quantia de pontos de glória."));

        this.setPermission("glorypoints.admin");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null) {
            Message.ERROR.send(sender, DefaultMessage.PLAYER_NOT_FOUND.getDefaultRawMessage());
            return;
        }

        Double value = Doubles.tryParse(args[1]);
        if (value == null || value.isNaN() || value.isInfinite() || value <= 0.0D) {
            Message.ERROR.send(sender, "Quantia inválida.");
            return;
        }

        GloryPointsAPI.withdraw(offlinePlayer.getName(), value);

        sender.sendMessage(
                String.format(
                        "§eVocê removeu §f%s pontos de glória §ede §f%s§e.",
                        NumberUtils.format(value),
                        offlinePlayer.getName()
                )
        );
    }
}
