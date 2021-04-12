package br.com.legion.tablist;

import br.com.idea.api.shared.messages.MessageUtils;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.promise.Promise;
import me.lucko.helper.scoreboard.PacketScoreboard;
import me.lucko.helper.scoreboard.PacketScoreboardProvider;
import me.lucko.helper.scoreboard.ScoreboardObjective;
import me.lucko.helper.scoreboard.ScoreboardTeam;
import me.lucko.helper.utils.Players;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.EnumMap;
import java.util.Map;

@Plugin(name = "tablist-plugin", hardDepends = {"helper"})
public class TablistPlugin extends ExtendedJavaPlugin {

    private static final MetadataKey<PlayerTab> TAB_KEY = MetadataKey.create("tab-plugin", PlayerTab.class);

    private static Chat chat;

    @Override
    protected void enable() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();

        PacketScoreboard scoreboard = getService(PacketScoreboardProvider.class).getScoreboard();

        Events.subscribe(PlayerJoinEvent.class)
                .handler(e -> Schedulers.async().run(() -> {
                    Rank rank = Rank.determine(e.getPlayer());

                    PlayerTab tab = new PlayerTab(e.getPlayer(), scoreboard);
                    for (Player other : Players.all()) {
                        Rank r = Rank.determine(other);
                        tab.handleJoin(other, r);
                    }

                    Metadata.provideForPlayer(e.getPlayer()).put(TAB_KEY, tab);

                    for (PlayerTab otherTab : Metadata.players().getAllWithKey(TAB_KEY).values()) {
                        otherTab.handleJoin(e.getPlayer(), rank);
                    }
                }))
                .bindWith(this);

        Events.subscribe(PlayerQuitEvent.class)
                .handler(e -> Schedulers.async().run(() -> {
                    for (PlayerTab otherTab : Metadata.players().getAllWithKey(TAB_KEY).values()) {
                        otherTab.handleQuit(e.getPlayer());
                    }
                }))
                .bindWith(this);
    }

    public void refresh() {
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

        private final EnumMap<Rank, ScoreboardTeam> teams = new EnumMap<>(Rank.class);

        public PlayerTab(Player player, PacketScoreboard sb) {
            player.setHealth(player.getHealth());

            ScoreboardObjective showHealth = sb.createPlayerObjective(player, "showHealth", DisplaySlot.BELOW_NAME);
            showHealth.setDisplayName(ChatColor.RED + "❤");

            for (Rank rank : Rank.values()) {
                String id = (rank.ordinal() + 1) + "-rel";

                ScoreboardTeam team = sb.createPlayerTeam(player, id);
                team.setPrefix(rank.prefix);

                this.teams.put(rank, team);
            }

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

        MASTER("&6[Master] ");

        @Getter
        private final String prefix;

        Rank(String prefix) {
            this.prefix = MessageUtils.translateColorCodes(prefix);
        }

        public static Rank determine(Player p) {
            String primaryGroup = TablistPlugin.chat.getPrimaryGroup(p);
            return valueOf(primaryGroup);
        }
    }
}
