package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.ConfirmInventory;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.echo.packets.GuildDisbandPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

public class DisbandSubCommand extends GuildSubCommand {

    public DisbandSubCommand() {
        super("desfazer", GuildRole.LEADER, "terminar", "excluir");
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        ConfirmInventory confirmInventory = ConfirmInventory.of(event -> {
            if (relation == null || relation.getRole() != GuildRole.LEADER) {
                Message.ERROR.send(player, "Você não é o líder da guilda.");
                player.closeInventory();
                return;
            }

            boolean result = GuildsProvider.Repositories.GUILDS.provide().delete(relation.getGuildId());

            GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(user.getId());
            GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateClan(relation.getGuildId());
            GuildsProvider.Cache.Local.GUILDS.provide().invalidateId(relation.getGuildId());

            if (!result) {
                Message.ERROR.send(player, "Algo de errado aconteceu, tente novamente.");
                player.closeInventory();
                return;
            }

            GuildsFrameworkProvider.Redis.ECHO.provide().publishToCurrentServer(
                    new GuildDisbandPacket(relation.getGuildId()));

            Message.SUCCESS.send(player, "Você desfez sua guilda.");
        }, event -> {
            Message.ERROR.send(player, "Você cancelou a ação de desfazer sua guilda.");
        }, null);

        player.openInventory(confirmInventory.make("&cSua facção será excluida."));
    }
}
