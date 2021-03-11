package br.com.legion.guilds.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class GuildSubCommand extends CustomCommand {

    private final GuildRole rank;

    public GuildSubCommand(String name, String... aliases) {
        this(name, null, aliases);
    }

    public GuildSubCommand(String name, GuildRole role, String... aliases) {
        super(name, CommandRestriction.IN_GAME, aliases);

        this.rank = role;
    }

    @Override
    public void onCommand(CommandSender sender, User handle, String[] args) {
        GuildUserRelation relation = GuildsProvider.Cache.Local.USERS_RELATIONS.provide().getByUser(handle.getId());

        if (rank != null) {
            if (relation == null) {
                Message.ERROR.send(sender, "Você precisa fazer parte de uma guilda.");
                return;
            }

            if (rank.ordinal() > relation.getRole().ordinal()) {
                Message.ERROR.send(sender, String.format("Você precisa ter o rank %s.", rank.getDisplayName()));
                return;
            }

            Guild guild = GuildsProvider.Cache.Local.GUILDS.provide().getById(relation.getGuildId());
            if (guild != null) {
                guild.doMaintenance();
            }
        }

        onCommand((Player) sender, handle, relation, args);
    }

    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        onCommand(player, user, args);
    }

    public void onCommand(Player player, User user, String[] args) {
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return ImmutableList.of();
    }
}
