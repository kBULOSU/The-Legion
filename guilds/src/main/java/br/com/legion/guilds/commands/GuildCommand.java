package br.com.legion.guilds.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.spigot.commands.CustomCommand;

public class GuildCommand extends CustomCommand {

    public GuildCommand() {
        super("guild", CommandRestriction.IN_GAME, "guilda");

        //sub comandos
    }
}
