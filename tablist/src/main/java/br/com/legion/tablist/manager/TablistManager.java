package br.com.legion.tablist.manager;

import br.com.idea.api.shared.messages.MessageUtils;
import br.com.legion.tablist.TablistPlugin;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.Services;
import me.lucko.helper.config.ConfigFactory;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.scoreboard.PacketScoreboard;
import me.lucko.helper.scoreboard.PacketScoreboardProvider;
import me.lucko.helper.scoreboard.ScoreboardTeam;
import me.lucko.helper.text3.Text;
import me.lucko.helper.utils.Players;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.nio.file.Path;
import java.util.Map;

public class TablistManager {

    private static BaseComponent[] HEADER;
    private static BaseComponent[] FOOTER;

    private static final MetadataKey<PlayerTab> TAB_KEY = MetadataKey.create("rank-tab", PlayerTab.class);

    public static void enable(TablistPlugin plugin) {
        try {
            Path resolve = plugin.getDataFolder().toPath().resolve("config.conf");

            String[] headerContents = ConfigFactory.hocon().load(resolve)
                    .getNode("header")
                    .getList(TypeToken.of(String.class))
                    .stream()
                    .map(MessageUtils::translateColorCodes)
                    .toArray(String[]::new);

            ComponentBuilder header = new ComponentBuilder("");

            for (String content : headerContents) {
                header.append(content);
            }

            HEADER = header.create();

            ////

            String[] footerContents = ConfigFactory.hocon().load(resolve)
                    .getNode("footer")
                    .getList(TypeToken.of(String.class))
                    .stream()
                    .map(MessageUtils::translateColorCodes)
                    .toArray(String[]::new);

            ComponentBuilder footer = new ComponentBuilder("");

            for (String content : footerContents) {
                footer.append(content);
            }

            FOOTER = footer.create();

        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        ///////////

        PacketScoreboard scoreboard = Services.load(PacketScoreboardProvider.class).getScoreboard();

        Events.subscribe(PlayerJoinEvent.class)
                .handler(e -> Schedulers.async().run(() -> {
                    Rank rank = Rank.determine(e.getPlayer());

                    PlayerTab tab = new PlayerTab(e.getPlayer(), scoreboard);
                    for (Player other : Players.all()) {
                        Rank r = Rank.determine(other);
                        tab.handleJoin(other, r);
                    }

                    if (HEADER != null && FOOTER != null) {
                        e.getPlayer().setPlayerListHeaderFooter(HEADER, FOOTER);
                    }

                    Metadata.provideForPlayer(e.getPlayer()).put(TAB_KEY, tab);

                    for (PlayerTab otherTab : Metadata.players().getAllWithKey(TAB_KEY).values()) {
                        otherTab.handleJoin(e.getPlayer(), rank);
                    }
                }))
                .bindWith(plugin);

        Events.subscribe(PlayerQuitEvent.class)
                .handler(e -> Schedulers.async().run(() -> {
                    // update the tabs for other players
                    for (PlayerTab otherTab : Metadata.players().getAllWithKey(TAB_KEY).values()) {
                        otherTab.handleQuit(e.getPlayer());
                    }
                }))
                .bindWith(plugin);

        LuckPermsProvider.get().getEventBus().subscribe(UserDataRecalculateEvent.class, event -> refresh());
    }

    private static void refresh() {
        Promise.completed(Players.all())
                .thenAcceptAsync(players -> {
                    for (Player p : players) {
                        Rank r = Rank.determine(p);
                        for (PlayerTab otherTab : Metadata.players().getAllWithKey(TAB_KEY).values()) {
                            otherTab.handleJoin(p, r);
                        }
                    }
                });
    }

    private static final class PlayerTab {
        private final Map<Rank, ScoreboardTeam> teams = Maps.newEnumMap(Rank.class);

        public PlayerTab(Player player, PacketScoreboard sb) {

            for (Rank rank : Rank.VALS) {
                String id = (rank.ordinal() + 1) + "-rank";

                ScoreboardTeam team = sb.createPlayerTeam(player, id);
                team.setPrefix(rank.prefix);
                this.teams.put(rank, team);
            }

            // mark ourselves
            handleJoin(player, Rank.determine(player));
        }

        public void handleJoin(Player other, Rank otherRank) {
            for (Map.Entry<Rank, ScoreboardTeam> team : this.teams.entrySet()) {
                if (otherRank.equals(team.getKey())) {
                    team.getValue().addPlayer(other);
                } else {
                    team.getValue().removePlayer(other);
                }
            }
        }

        public void handleQuit(Player other) {
            for (ScoreboardTeam team : this.teams.values()) {
                team.removePlayer(other);
            }
        }
    }

    private enum Rank {
        OWNER("&c[Owner] "),
        ADMIN("&b[Admin] "),
        VIP("&a[VIP] "),
        MEMBER("&7[Member] ");

        public static final Rank[] VALS = Rank.values();

        private final String prefix;

        Rank(String prefix) {
            this.prefix = Text.colorize(prefix);
        }

        public static Rank determine(Player p) {
            return MEMBER;
        }
    }
}
