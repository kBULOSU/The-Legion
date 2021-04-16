package com.nametagedit.plugin.storage;

import com.nametagedit.plugin.api.data.GroupData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface AbstractConfig {

    void load();

    void reload();

    void shutdown();

    void load(Player player, boolean loggedIn);

    void save(GroupData... groupData);

    void delete(GroupData groupData);

    void add(GroupData groupData);

    void orderGroups(CommandSender commandSender, List<String> order);

}