package br.com.legion.guilds.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.legion.guilds.commands.subcommands.*;
import br.com.legion.guilds.commands.subcommands.banksubcommand.BankSubCommand;
import br.com.legion.guilds.commands.subcommands.levelsubcommand.LevelSubCommand;
import br.com.legion.guilds.commands.subcommands.menusubcommand.MenuSubCommand;
import br.com.legion.guilds.commands.subcommands.relationsubcommand.RelationSubCommand;

public class GuildCommand extends CustomCommand {

    public GuildCommand() {
        super("guilda", CommandRestriction.IN_GAME, "guild");

        //sub comandos
        registerSubCommand(new AcceptSubCommand(), "Aceite um convite de guilda.");
        registerSubCommand(new AllySubCommand(), "Convide uma guilda para uma aliança.");
        registerSubCommand(new CreateSubCommand(), "Crie uma guilda.");
        registerSubCommand(new DemoteSubCommand(), "Rebaixe um jogador da guilda.");
        registerSubCommand(new DisbandSubCommand(), "Desfaça sua guilda.");
        registerSubCommand(new InfoSubCommand(), "Colete informações sobre uma guilda.");
        registerSubCommand(new InviteSubCommand(), "Convide um jogador para a guilda.");
        registerSubCommand(new KickSubCommand(), "Expulse um jogador da guilda.");
        registerSubCommand(new LeaveSubCommand(), "Saia de sua guilda.");
        registerSubCommand(new ListSubCommand(), "Liste as guildas do servidor.");
        registerSubCommand(new MembersSubCommand(), "Liste os membros de uma guilda.");
        registerSubCommand(new PromoteSubCommand(), "Promova um jogador da guilda.");
        registerSubCommand(new TransferSubCommand(), "Transfira a liderança da guilda.");
        registerSubCommand(new RelationSubCommand(), "Gerencie a relação com uma guilda.");
        registerSubCommand(new BankSubCommand(), "Gerencie o banco da guilda.");
        registerSubCommand(new MenuSubCommand(), "Abra o menu da guilda.");
        registerSubCommand(new LevelSubCommand(), "Abra o menu de level.");
    }
}
