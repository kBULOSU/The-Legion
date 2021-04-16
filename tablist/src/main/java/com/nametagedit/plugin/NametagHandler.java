package com.nametagedit.plugin;

import com.nametagedit.plugin.api.data.GroupData;
import com.nametagedit.plugin.api.data.INametag;
import com.nametagedit.plugin.api.events.NametagFirstLoadedEvent;
import com.nametagedit.plugin.storage.AbstractConfig;
import com.nametagedit.plugin.storage.flatfile.FlatFileConfig;
import com.nametagedit.plugin.utils.Configuration;
import com.nametagedit.plugin.utils.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
@Setter
public class NametagHandler implements Listener {

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private boolean debug;

    @Getter(AccessLevel.NONE)
    private boolean tabListEnabled;

    private AbstractConfig abstractConfig;
    private Configuration config;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<GroupData> groupData = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private NametagEdit plugin;

    private NametagManager nametagManager;

    public NametagHandler(NametagEdit plugin, NametagManager nametagManager) {
        this.config = getCustomConfig(plugin);
        this.plugin = plugin;
        this.nametagManager = nametagManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.applyConfig();

        abstractConfig = new FlatFileConfig(plugin, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                abstractConfig.load();
            }
        }.runTaskAsynchronously(plugin);
    }

    private Configuration getCustomConfig(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveDefaultConfig();

            Configuration newConfig = new Configuration(file);
            newConfig.reload(true);
            return newConfig;
        } else {
            Configuration oldConfig = new Configuration(file);
            oldConfig.reload(false);

            file.delete();
            plugin.saveDefaultConfig();

            Configuration newConfig = new Configuration(file);
            newConfig.reload(true);

            for (String key : oldConfig.getKeys(false)) {
                if (newConfig.contains(key)) {
                    newConfig.set(key, oldConfig.get(key));
                }
            }

            newConfig.save();
            return newConfig;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        nametagManager.reset(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        nametagManager.sendTeams(player);
    }

    private void handleClear(UUID uuid, String player) {
        nametagManager.reset(player);
    }

    public void clearMemoryData() {
        try {
            readWriteLock.writeLock().lock();
            groupData.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void assignGroupData(List<GroupData> groupData) {
        try {
            readWriteLock.writeLock().lock();
            this.groupData = groupData;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void assignData(List<GroupData> groupData) {
        try {
            readWriteLock.writeLock().lock();
            this.groupData = groupData;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    boolean debug() {
        return debug;
    }

    void toggleDebug() {
        debug = !debug;
        config.set("Debug", debug);
        config.save();
    }

    void addGroup(GroupData data) {
        abstractConfig.add(data);

        try {
            readWriteLock.writeLock().lock();
            groupData.add(data);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    void deleteGroup(GroupData data) {
        abstractConfig.delete(data);

        try {
            readWriteLock.writeLock().lock();
            groupData.remove(data);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public List<GroupData> getGroupData() {
        try {
            readWriteLock.writeLock().lock();
            return new ArrayList<>(groupData);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public GroupData getGroupData(String key) {
        for (GroupData groupData : getGroupData()) {
            if (groupData.getGroupName().equalsIgnoreCase(key)) {
                return groupData;
            }
        }

        return null;
    }

    public String formatWithPlaceholders(Player player, String input, boolean limitChars) {
        if (input == null) return "";
        if (player == null) return input;

        return Utils.format(input, limitChars);
    }

    public void reload() {
        config.reload(true);
        applyConfig();
        nametagManager.reset();
        abstractConfig.reload();
    }

    private void applyConfig() {
        this.tabListEnabled = true;
    }

    public void applyTags() {
        if (!Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    applyTags();
                }
            }.runTask(plugin);
            return;
        }

        for (Player online : Utils.getOnline()) {
            if (online != null) {
                applyTagToPlayer(online, false);
            }
        }
    }

    public void applyTagToPlayer(final Player player, final boolean loggedIn) {
        if (Bukkit.isPrimaryThread()) {
            Schedulers.async().run(() -> applyTagToPlayer(player, loggedIn));
            return;
        }

        INametag tempNametag = null;
        for (GroupData group : getGroupData()) {
            if (group == null) {
                continue;
            }

            if (player.hasPermission(group.getBukkitPermission())) {
                tempNametag = group;
                break;
            }
        }

        if (tempNametag == null) return;

        final INametag nametag = tempNametag;

        Schedulers.sync().run(() -> {
            String prefix = nametag.getPrefix();
            nametagManager.setNametag(player.getName(), formatWithPlaceholders(player, prefix, true),
                    formatWithPlaceholders(player, nametag.getSuffix(), true), nametag.getSortPriority());

            if (!tabListEnabled) {
                player.setPlayerListName(Utils.format("&f" + player.getPlayerListName()));
            } else {
                player.setPlayerListName(null);
            }

            if (loggedIn) {
                Bukkit.getPluginManager().callEvent(new NametagFirstLoadedEvent(player, nametag));
            }
        });
    }
}