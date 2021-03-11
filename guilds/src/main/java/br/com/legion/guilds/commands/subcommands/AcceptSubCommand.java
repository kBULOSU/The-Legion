package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.PaginateInventory;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.echo.packets.UserJoinedGuildPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.Set;

public class AcceptSubCommand extends GuildSubCommand {

    public AcceptSubCommand() {
        super("aceitar");

        registerArgument(new Argument("tag", "Tag da guilda que você aceitará o convite.", false));
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        if (relation != null) {
            Message.ERROR.send(player, "Você já está em uma guilda.");
            return;
        }

        if (args.length != 1) {

            PaginateInventory.PaginateInventoryBuilder inventory = PaginateInventory.builder();

            Set<Integer> invites = GuildsProvider.Cache.Redis.GUILD_INVITATIONS.provide().getInvitations(user.getId());

            invites.forEach(guildId -> {
                inventory.item(new ItemStack(Material.BANNER), event -> {
                    player.performCommand("guilda aceitar " + GuildsProvider.Cache.Local.GUILDS.provide().getById(guildId).getTag());
                });
            });

            player.openInventory(inventory.build("Convites"));
            return;
        }

        String tag = args[0];

        Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getByTag(tag);
        if (guild == null) {
            Message.ERROR.send(player, "Esta guilda não existe.");
            return;
        }

        if (!GuildsProvider.Cache.Redis.GUILD_INVITATIONS.provide().hasInvite(user.getId(), guild.getId())) {
            Message.ERROR.send(player, "Você não não tem um convite para esta guilda.");
            return;
        }

        Set<User> allUsers = GuildUtils.getUsers(guild.getId());

        if (allUsers.size() >= guild.getMaxMembers()) {
            Message.ERROR.send(player, String.format(
                    "A guilda %s já atingiu o número máximo de %s jogadores.",
                    guild.getDisplayName(),
                    guild.getMaxMembers()
            ));
            return;
        }

        relation = new GuildUserRelation(user.getId(), guild.getId(), GuildRole.MEMBER, new Date());

        boolean success = GuildsProvider.Repositories.USERS_RELATIONS.provide().update(relation);
        GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(user.getId());

        if (!success) {
            Message.ERROR.send(player, "Ocorreu um erro ao tentar aceitar o convite.");
            return;
        }

        GuildsProvider.Cache.Redis.GUILD_INVITATIONS.provide().clearInvitations(user.getId());

        GuildsFrameworkProvider.Redis.ECHO.provide()
                .publishToAll(new UserJoinedGuildPacket(guild.getId(), user.getId(), UserJoinedGuildPacket.Reason.INVITATION));

        Message.SUCCESS.send(player, String.format("Você aceitou o convite da guilda [%s] %s.", guild.getTag(), guild.getName()));
    }
}
