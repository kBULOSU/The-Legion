package br.com.legion.guilds.commands.subcommands.banksubcommand;

import br.com.idea.api.shared.user.User;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.commands.subcommands.banksubcommand.inventories.IndexInventory;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.entity.Player;

public class BankSubCommand extends GuildSubCommand {

    public BankSubCommand() {
        super("banco", GuildRole.CAPTAIN, "bank", "cofre");
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());
        player.openInventory(new IndexInventory(guild));
    }
}
