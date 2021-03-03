package br.com.legion.guilds.commands.subcommands.relationsubcommand;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.commands.subcommands.relationsubcommand.inventories.IndexInventory;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

public class RelationSubCommand extends GuildSubCommand {

    public RelationSubCommand() {
        super("relacao", "relação", "rel");

        registerArgument(new Argument("tag", "Tag da guilda que você irá convidar para aliança.", false));
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {

        Guild guild;

        if (args.length < 1) {

            if (relation == null) {
                player.sendMessage(getUsage(player, "relacao"));
                return;
            }

            guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());

        } else {
            guild = GuildsProvider.Cache.Local.GUILDS.provide().getByTag(args[0]);

            if (guild == null) {
                Message.ERROR.send(player, String.format("A guilda %s não existe.", args[0]));
                return;
            }
        }

        boolean isLeader = relation != null && relation.getGuildId() == guild.getId() && relation.getRole() == GuildRole.LEADER;

        player.openInventory(new IndexInventory(guild, user, isLeader));
    }
}
