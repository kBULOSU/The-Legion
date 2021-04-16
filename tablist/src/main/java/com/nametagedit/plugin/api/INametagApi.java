package com.nametagedit.plugin.api;

import com.nametagedit.plugin.api.data.FakeTeam;
import com.nametagedit.plugin.api.data.GroupData;
import com.nametagedit.plugin.api.data.Nametag;
import org.bukkit.entity.Player;

import java.util.List;

public interface INametagApi {

    FakeTeam getFakeTeam(Player player);

    Nametag getNametag(Player player);

    void clearNametag(Player player);

    void reloadNametag(Player player);

    void clearNametag(String player);

    void setPrefix(Player player, String prefix);

    void setSuffix(Player player, String suffix);

    void setPrefix(String player, String prefix);

    void setSuffix(String player, String suffix);

    void setNametag(Player player, String prefix, String suffix);

    void setNametag(String player, String prefix, String suffix);

    List<GroupData> getGroupData();

    void saveGroupData(GroupData... groupData);

    void applyTags();

    void applyTagToPlayer(Player player, boolean loggedIn);
}