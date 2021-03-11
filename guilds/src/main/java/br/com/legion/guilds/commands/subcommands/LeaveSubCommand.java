package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.ConfirmInventory;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.echo.packets.UserLeftGuildPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

public class LeaveSubCommand extends GuildSubCommand {

    public LeaveSubCommand() {
        super("sair", GuildRole.MEMBER);
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation runtimeRelation, String[] args) {
        if (runtimeRelation.getRole() == GuildRole.LEADER) {
            Message.ERROR.send(player, "Você precisa transferir ou terminar sua guilda antes de sair.");
            return;
        }

        ConfirmInventory confirmInventory = ConfirmInventory.of(event -> {
            GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user.getId());

            if (relation == null) {
                Message.ERROR.send(player, "Você não é membro de uma guilda.");
                player.closeInventory();
                return;
            }

            if (relation.getRole() == GuildRole.LEADER) {
                Message.ERROR.send(player, "Você precisa transferir ou terminar sua guilda antes de sair.");
                player.closeInventory();
                return;
            }

            GuildsProvider.Repositories.USERS_RELATIONS.provide().removeByUser(user.getId());
            GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(user.getId());
            GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateClan(relation.getGuildId());

            GuildsFrameworkProvider.Redis.ECHO.provide().publishToAll(new UserLeftGuildPacket(
                    relation.getGuildId(),
                    user.getId(),
                    UserLeftGuildPacket.Reason.LEAVE
            ));

            Message.SUCCESS.send(player, "Você saiu da guilda.");

        }, event -> {
            Message.ERROR.send(player, "Você cancelou a ação de sair de sua guilda.");
        }, null);

        player.openInventory(confirmInventory.make("Você sairá da guilda."));
    }
}
