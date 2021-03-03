package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.echo.packets.relation.GuildRelationCreatedPacket;
import br.com.legion.guilds.echo.packets.relation.GuildRelationDeletedPacket;
import br.com.legion.guilds.echo.packets.relation.GuildRelationInvitePacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.relation.guild.GuildRelation;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

import java.util.Set;

public class AllySubCommand extends GuildSubCommand {

    public AllySubCommand() {
        super("alianca", GuildRole.LEADER, "aliada", "aliados", "ally", "aliadas", "aliar");

        registerArgument(new Argument("tag", "Tag da guilda que você irá convidar para aliança.", true));
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        Guild target = GuildsProvider.Cache.Local.GUILDS.provide().getByTag(args[0]);

        if (target == null) {
            Message.ERROR.send(player, String.format("A guilda &7%s &cnão existe.", args[0].toUpperCase()));
            return;
        }

        if (target.getId().equals(relation.getGuildId())) {
            Message.ERROR.send(player, "Escolha outra guilda para fazer aliança.");
            return;
        }

        Set<Integer> ownsRelations = GuildsProvider.Cache.Local.GUILDS_RELATIONS
                .provide().getRelationships(relation.getGuildId(), GuildRelation.ALLY);

        if (args.length == 2 && args[1].equalsIgnoreCase("desfazer")) {
            if (!ownsRelations.contains(target.getId())) {
                Message.ERROR.send(player, String.format("Sua guilda não é aliada da guilda %s&c.", target.getDisplayName()));
                return;
            }

            GuildsProvider.Repositories.GUILDS_RELATIONS.provide().delete(relation.getGuildId(), target.getId());
            GuildsProvider.Cache.Local.GUILDS_RELATIONS.provide().invalidate(relation.getGuildId(), target.getId());

            GuildsFrameworkProvider.Redis.ECHO.provide().publishToCurrentServer(
                    new GuildRelationDeletedPacket(relation.getGuildId(), target.getId(), GuildRelation.ALLY));

            Message.INFO.send(player, String.format("Sua guilda desfez a aliança com a guilda %s&e.", target.getDisplayName()));

            return;
        }

        if (ownsRelations.size() >= 2) {
            Message.ERROR.send(player, "Sua guilda já atingiu o limite de 2 alianças.");
            return;
        }

        if (ownsRelations.contains(target.getId())) {
            Message.ERROR.send(player, String.format("A guilda %s &cjá é aliado da sua guilda.", target.getDisplayName()));
            return;
        }

        if (GuildsProvider.Cache.Redis.ALLY_INVITATIONS.provide().hasInvite(target.getId(), relation.getGuildId())) {
            GuildsProvider.Cache.Redis.ALLY_INVITATIONS.provide().removeInvitation(target.getId(), relation.getGuildId());

            GuildsProvider.Repositories.GUILDS_RELATIONS.provide().insert(relation.getGuildId(), target.getId(), GuildRelation.ALLY);
            GuildsProvider.Cache.Local.GUILDS_RELATIONS.provide().invalidate(relation.getGuildId(), target.getId());

            GuildsFrameworkProvider.Redis.ECHO.provide().publishToCurrentServer(
                    new GuildRelationCreatedPacket(relation.getGuildId(), target.getId(), GuildRelation.ALLY));

            Message.SUCCESS.send(player, String.format("Sua guilda começou uma aliança com a guilda %s&a.", target.getDisplayName()));

            return;
        }

        if (GuildsProvider.Cache.Redis.ALLY_INVITATIONS.provide().hasInvite(relation.getGuildId(), target.getId())) {
            Message.ERROR.send(player, "Já existe um convite pendente para esta guilda.");
            return;
        }

        GuildsProvider.Cache.Redis.ALLY_INVITATIONS.provide().putInvitation(relation.getGuildId(), target.getId());
        GuildsFrameworkProvider.Redis.ECHO.provide().publishToCurrentServer(
                new GuildRelationInvitePacket(relation.getGuildId(), target.getId(), GuildRelation.ALLY));

        Message.SUCCESS.send(player, String.format("Você convidou a guilda %s &apara uma aliança.", target.getDisplayName()));
    }
}
