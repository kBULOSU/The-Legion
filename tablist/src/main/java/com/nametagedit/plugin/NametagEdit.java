package com.nametagedit.plugin;

import com.nametagedit.plugin.api.INametagApi;
import com.nametagedit.plugin.api.NametagAPI;
import com.nametagedit.plugin.hooks.HookLuckPerms;
import com.nametagedit.plugin.listeners.HeaderAndFooterListener;
import com.nametagedit.plugin.packets.PacketWrapper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

@Getter
public class NametagEdit extends JavaPlugin {

    private static INametagApi api;

    private NametagHandler handler;
    private NametagManager manager;

    public static INametagApi getApi() {
        return api;
    }

    @Override
    public void onEnable() {
        testCompat();
        if (!isEnabled()) return;

        manager = new NametagManager(this);
        handler = new NametagHandler(this, manager);

        PluginManager pluginManager = Bukkit.getPluginManager();
        if (checkShouldRegister()) {
            pluginManager.registerEvents(new HookLuckPerms(handler), this);
        }

        pluginManager.registerEvents(new HeaderAndFooterListener(this), this);

        if (api == null) {
            api = new NametagAPI(handler, manager);
        }
    }

    @Override
    public void onDisable() {
        manager.reset();
        handler.getAbstractConfig().shutdown();
    }

    private boolean checkShouldRegister() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) return false;
        getLogger().info("Found " + "LuckPerms" + "! Hooking in.");
        return true;
    }

    private void testCompat() {
        PacketWrapper wrapper = new PacketWrapper("TEST", "&f", "", 0, new ArrayList<>());
        wrapper.send();
        if (wrapper.error == null) return;
        Bukkit.getPluginManager().disablePlugin(this);
    }
}