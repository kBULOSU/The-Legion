package br.com.legion.tablist;

import br.com.legion.tablist.manager.TablistManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.plugin.ap.PluginDependency;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

@Plugin(
        name = "L-TabList",
        version = "0.1",
        authors = "yiatzz",
        depends = {@PluginDependency("helper"), @PluginDependency("ProtocolLib"), @PluginDependency("LuckPerms")}
)
public class TablistPlugin extends ExtendedJavaPlugin {

    @Override
    protected void enable() {
        saveResource("config.conf", false);

        TablistManager.enable(this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.HIGH,
                PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                List<PlayerInfoData> data = event.getPacket().getPlayerInfoDataLists().read(0);

                for (PlayerInfoData datum : data) {
                    int indexOf = data.indexOf(datum);

                    datum = new PlayerInfoData(datum.getProfile(), RandomUtils.nextInt(10, 20),
                            datum.getGameMode(), datum.getDisplayName());

                    data.set(indexOf, datum);
                }

                event.getPacket().getPlayerInfoDataLists().write(0, data);
            }
        });
    }
}
