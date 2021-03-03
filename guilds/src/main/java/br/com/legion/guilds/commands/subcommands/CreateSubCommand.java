package br.com.legion.guilds.commands.subcommands;

import br.com.idea.api.shared.commands.Argument;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.shared.user.User;
import br.com.idea.api.spigot.inventory.ConfirmInventory;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.glory.points.GloryPointsAPI;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.commands.GuildSubCommand;
import br.com.legion.guilds.echo.packets.UserJoinedGuildPacket;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import com.google.common.base.Joiner;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Date;

public class CreateSubCommand extends GuildSubCommand {

    public CreateSubCommand() {
        super("criar");

        registerArgument(new Argument("tag", "A tag da guilda."));
        registerArgument(new Argument("nome", "O nome da guilda."));
    }

    @Override
    public void onCommand(Player player, User user, GuildUserRelation relation, String[] args) {
        if (relation != null) {
            Message.ERROR.send(player, "Você já faz parte de uma guilda.");
            return;
        }

        String tag = args[0];
        String name = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));

        if (tag.length() > GuildsConstants.TAG_MAX_LENGTH || tag.length() < GuildsConstants.TAG_MIN_LENGTH) {
            Message.ERROR.send(player, String.format(
                    "A tag da guilda deve conter entre %s e %s caracteres.",
                    GuildsConstants.TAG_MIN_LENGTH,
                    GuildsConstants.TAG_MAX_LENGTH
            ));
            return;
        }

        if (!GuildsConstants.TAG_PATTERN.matcher(tag).matches()) {
            Message.ERROR.send(player, "A tag da guilda não pode conter caracteres especiais.");
            return;
        }

        if (name.length() < GuildsConstants.NAME_MIN_LENGTH || name.length() > GuildsConstants.NAME_MAX_LENGTH) {
            Message.ERROR.send(
                    player,
                    String.format(
                            "O nome de sua guilda deve conter de %s a %s caracteres.",
                            GuildsConstants.NAME_MIN_LENGTH,
                            GuildsConstants.NAME_MAX_LENGTH
                    )
            );
            return;
        }

        if (!GuildsConstants.NAME_PATTERN.matcher(name).matches()) {
            Message.ERROR.send(player, "O nome de sua guilda não pode conter caracteres especiais.");
            return;
        }

        Guild guildByTag = GuildsProvider.Cache.Local.GUILDS.provide().getByTag(tag);

        if (guildByTag != null) {
            Message.ERROR.send(player, "Já existe outra guilda utilizando a tag \"&f" + tag + "&c\".");
            return;
        }

        boolean anyMatchWithName = GuildsProvider.Repositories.GUILDS.provide().fetchByName(name) != null;

        if (anyMatchWithName) {
            Message.ERROR.send(player, "Já existe outra guilda utilizando o nome \"&f" + name + "&c\".");
            return;
        }

        if (!GloryPointsAPI.has(player.getName(), GuildsConstants.Config.PRICE_TO_CREATE)) {
            Message.ERROR.send(player, "Você não possui pontos de glória suficientes para criar uma guilda.");
            return;
        }

        int playTimeInTicks = player.getStatistic(Statistic.PLAY_ONE_TICK);
        int playTimeInHours = playTimeInTicks / 20 / 60 / 60;

        if (playTimeInHours < GuildsConstants.Config.HOURS_TO_CREATE) {
            Message.ERROR.send(player, "Você não possui horas de jogo suficiente para criar uma guilda.");
            return;
        }

        ConfirmInventory confirmInventory = new ConfirmInventory(
                targetEvent -> {

                    Guild guild = GuildsProvider.Repositories.GUILDS.provide()
                            .create(tag, name, 1, GuildsConstants.GUILD_DEFAULT_MAX_MEMBERS, 0);

                    GuildsProvider.Cache.Local.USERS_RELATIONS.provide().invalidateUser(user.getId());

                    if (guild == null) {
                        GuildsProvider.Cache.Local.GUILDS.provide().invalidateTag(tag);

                        Message.ERROR.send(player, "Não foi possivel criar sua guilda.");
                        return;
                    }

                    GloryPointsAPI.withdraw(player.getName(), GuildsConstants.Config.PRICE_TO_CREATE);

                    GuildUserRelation relation1 = new GuildUserRelation(user.getId(), guild.getId(), GuildRole.LEADER, new Date());

                    GuildsProvider.Repositories.USERS_RELATIONS.provide().update(relation1);

                    GuildsFrameworkProvider.Redis.ECHO.provide().publishToCurrentServer(
                            new UserJoinedGuildPacket(guild.getId(), user.getId(), UserJoinedGuildPacket.Reason.GUILD_CREATED));

                    Message.SUCCESS.send(player, "Sua guilda foi criada com sucesso!");
                },
                targetEvent -> {
                    // nada
                },
                null
        );

        player.openInventory(
                confirmInventory.make(
                        "Ao confirmar você",
                        "pagará " + NumberUtils.toK(GuildsConstants.Config.PRICE_TO_CREATE)
                )
        );

    }
}
