package br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.icons;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.CustomInventory;
import br.com.idea.api.spigot.misc.utils.ItemBuilder;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.subcommands.MembersSubCommand;
import br.com.legion.guilds.commands.subcommands.menusubcommand.inventories.MenuIcon;
import br.com.legion.guilds.misc.utils.GuildUtils;
import br.com.legion.guilds.relation.guild.GuildRelation;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.function.Supplier;

public class GuildInformationIcon extends MenuIcon {

    public GuildInformationIcon(User user, Supplier<CustomInventory> back) {
        super(user, back);
    }

    @Override
    public ItemStack getIcon() {
        GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(user.getId());
        Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());

        ItemBuilder builder = GuildUtils.getBanner(guild, user)
                .name("&aInformações da Guilda");

        Set<User> onlineUsers = MembersSubCommand.ONLINE_USERS_CACHE.get(guild.getId());
        builder.lore(String.format(
                "&fMembros Online: &a%s",
                onlineUsers != null ? onlineUsers.size() : "&cNenhum"
        ));

        GuildUserRelation leader = GuildsProvider.Cache.Local.USERS_RELATIONS.provide()
                .getByGuildAndRole(guild.getId(), GuildRole.LEADER);
        builder.lore(String.format(
                "&fLíder: &a%s",
                leader != null ? ApiProvider.Cache.Local.USERS.provide().get(leader.getUserId()).getName() : "Inválido"
        ));

        Set<Integer> relationships = GuildsProvider.Cache.Local.GUILDS_RELATIONS.provide().getRelationships(guild.getId(), GuildRelation.ALLY);
        builder.lore(String.format(
                "&fAliadas: &a%s",
                relationships != null ? relationships.size() : "&cNenhum"
        ));

        return builder.make();
    }

    @Override
    public Runnable getRunnable() {
        return null;
    }

}
