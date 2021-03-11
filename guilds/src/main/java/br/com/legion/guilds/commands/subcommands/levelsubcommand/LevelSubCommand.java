package br.com.legion.guilds.commands.subcommands.levelsubcommand;

import br.com.idea.api.shared.user.User;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.relation.user.GuildRole;
import org.bukkit.entity.Player;

public class LevelSubCommand extends GuildSubCommand {

    public LevelSubCommand() {
        super("level", GuildRole.LEADER, "nivel");
    }

    @Override
    public void onCommand(Player player, User user, String[] args) {
        super.onCommand(player, user, args);
    }
}
