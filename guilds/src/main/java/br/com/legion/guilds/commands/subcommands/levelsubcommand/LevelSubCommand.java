package br.com.legion.guilds.commands.subcommands.levelsubcommand;

import br.com.idea.api.shared.user.User;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.commands.subcommands.levelsubcommand.inventories.IndexInventory;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

public class LevelSubCommand extends GuildSubCommand {

    public LevelSubCommand() {
        super("level", GuildRole.LEADER, "nivel");
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());
        player.openInventory(new IndexInventory(guild, relation));
    }
}
