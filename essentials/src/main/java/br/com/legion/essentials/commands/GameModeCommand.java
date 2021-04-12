package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.essentials.EssentialsConstants;
import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeCommand extends CustomCommand {

    public GameModeCommand() {
        super("gamemode", CommandRestriction.IN_GAME, "gm");

        setPermission("command.gamemode");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        if (args.length < 1) {
            Message.ERROR.send(sender, "Utilize: &7/gm <id> [player].");
            return;
        }

        Integer id = Ints.tryParse(args[0]);

        if (id == null) {
            Message.ERROR.send(sender, "Modo de jogo inválido.");
            return;
        }

        GameMode gameMode = GameMode.getByValue(id);

        if (gameMode == null) {
            Message.ERROR.send(sender, "Modo de jogo inválido.");
            return;
        }

        String gamemodePermission = EssentialsConstants.ALLOW_GAMEMODE.getOrDefault(gameMode, "command.gamemode");
        if (!player.hasPermission(gamemodePermission)) {
            Message.ERROR.send(sender, String.format(
                    "Péh, você não pode utilizar o gamemode %s!",
                    gameMode.name()
            ));
            return;
        }

        Player targetPlayer = null;

        if (args.length > 1 && player.isOp()) {

            targetPlayer = Bukkit.getPlayerExact(args[1]);

            if (targetPlayer == null) {
                Message.ERROR.send(sender, String.format("O jogador \"%s\" não está online.", args[1]));
                return;
            }
        }

        if (targetPlayer != null && player != targetPlayer) {
            String string = String.format("Game Mode de \"%s\" alterado para &f%s&a.", targetPlayer.getName(), gameMode.name());
            Message.SUCCESS.send(sender, string);

            targetPlayer.setGameMode(gameMode);
            return;
        }

        player.setGameMode(gameMode);
        Message.SUCCESS.send(sender, "Game Mode alterado para &f" + gameMode.name() + "&a.");

    }
}
