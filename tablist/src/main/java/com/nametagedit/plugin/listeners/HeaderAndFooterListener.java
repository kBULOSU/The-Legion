package com.nametagedit.plugin.listeners;

import com.nametagedit.plugin.NametagEdit;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HeaderAndFooterListener implements Listener {

    private final BaseComponent[] HEADER;
    private final BaseComponent[] FOOTER;

    public HeaderAndFooterListener(NametagEdit plugin) {
        FileConfiguration config = plugin.getConfig();

        HEADER = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', config.getString("header")))
                .create();

        FOOTER = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', config.getString("footer")))
                .create();
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        event.getPlayer().setPlayerListHeaderFooter(HEADER, FOOTER);
    }
}
