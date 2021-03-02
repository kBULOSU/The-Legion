package br.com.legion.guilds.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.legion.guilds.commands.subcommands.*;

public class GuildCommand extends CustomCommand {

    public GuildCommand() {
        super("guild", CommandRestriction.IN_GAME, "guilda");

        //sub comandos

        registerSubCommand(new CreateSubCommand(), "Crie uma guilda.");
        registerSubCommand(new DemoteSubCommand(), "Rebaixe um jogador da guilda.");
        registerSubCommand(new DisbandSubCommand(), "Desfaça sua guilda.");
        registerSubCommand(new InfoSubCommand(), "Colete informações sobre uma guilda.");
        registerSubCommand(new KickSubCommand(), "Expulse um jogador da guilda.");
        registerSubCommand(new LeaveSubCommand(), "Saia de sua guilda.");
        registerSubCommand(new ListSubCommand(), "Liste as guildas do servidor.");
        registerSubCommand(new MembersSubCommand(), "Liste os membros de uma guilda.");
        registerSubCommand(new PromoteSubCommand(), "Promova um jogador da guilda.");
        registerSubCommand(new TransferSubCommand(), "Transfira a liderança da guilda.");

    }
}
