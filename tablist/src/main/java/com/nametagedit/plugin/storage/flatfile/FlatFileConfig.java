package com.nametagedit.plugin.storage.flatfile;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.NametagHandler;
import com.nametagedit.plugin.api.data.GroupData;
import com.nametagedit.plugin.storage.AbstractConfig;
import com.nametagedit.plugin.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlatFileConfig implements AbstractConfig {

    private final NametagEdit plugin;
    private final NametagHandler handler;
    private File groupsFile;
    private YamlConfiguration groups;

    public FlatFileConfig(NametagEdit plugin, NametagHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    @Override
    public void load() {
        groupsFile = new File(plugin.getDataFolder(), "groups.yml");
        groups = Utils.getConfig(groupsFile, "groups.yml", plugin);
        loadGroups();

        new BukkitRunnable() {
            @Override
            public void run() {
                handler.applyTags();
            }
        }.runTask(plugin);
    }

    @Override
    public void reload() {
        handler.clearMemoryData();

        new BukkitRunnable() {
            @Override
            public void run() {
                load();
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void shutdown() {
        // NOTE: Nothing to do
    }

    @Override
    public void load(Player player, boolean loggedIn) {
        plugin.getHandler().applyTagToPlayer(player, loggedIn);
    }

    @Override
    public void save(final GroupData... data) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (GroupData groupData : data) {
                    storeGroup(groupData);
                }

                save(groups, groupsFile);
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void delete(GroupData groupData) {
        groups.set("Groups." + groupData.getGroupName(), null);
        save(groups, groupsFile);
    }

    @Override
    public void add(GroupData groupData) {
        // NOTE: Nothing to do
    }

    @Override
    public void orderGroups(CommandSender commandSender, List<String> order) {
        groups.set("Groups", null);
        for (String set : order) {
            GroupData groupData = handler.getGroupData(set);
            if (groupData != null) {
                storeGroup(groupData);
            }
        }

        for (GroupData groupData : handler.getGroupData()) {
            if (!groups.contains("Groups." + groupData.getGroupName())) {
                storeGroup(groupData);
            }
        }

        save(groups, groupsFile);
    }

    private void save(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGroups() {
        List<GroupData> groupData = new ArrayList<>();
        for (String groupName : groups.getConfigurationSection("Groups").getKeys(false)) {
            GroupData data = new GroupData();
            data.setGroupName(groupName);
            data.setPermission(groups.getString("Groups." + groupName + ".Permission", "nte.default"));
            data.setPrefix(groups.getString("Groups." + groupName + ".Prefix", ""));
            data.setSuffix(groups.getString("Groups." + groupName + ".Suffix", ""));
            data.setSortPriority(groups.getInt("Groups." + groupName + ".SortPriority", -1));
            groupData.add(data);
        }

        handler.assignGroupData(groupData);
    }

    private void storeGroup(GroupData groupData) {
        groups.set("Groups." + groupData.getGroupName() + ".Permission", groupData.getPermission());
        groups.set("Groups." + groupData.getGroupName() + ".Prefix", Utils.deformat(groupData.getPrefix()));
        groups.set("Groups." + groupData.getGroupName() + ".Suffix", Utils.deformat(groupData.getSuffix()));
        groups.set("Groups." + groupData.getGroupName() + ".SortPriority", groupData.getSortPriority());
    }
}