package br.com.legion.guilds.listeners;

import com.google.common.collect.Maps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.function.Consumer;

public class PlayerChatListener implements Listener {

    private static final Map<String, Consumer<AsyncPlayerChatEvent>> CONSUMERS =
            Maps.newConcurrentMap();

    public static void on(String playerName, Consumer<AsyncPlayerChatEvent> consumer) {
        CONSUMERS.put(playerName, consumer);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent event) {
        Consumer<AsyncPlayerChatEvent> remove = CONSUMERS.remove(event.getPlayer().getName());
        if (remove != null) {
            remove.accept(event);
        }
    }
}
