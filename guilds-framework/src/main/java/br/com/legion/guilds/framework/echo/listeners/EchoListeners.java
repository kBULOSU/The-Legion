package br.com.legion.guilds.framework.echo.listeners;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.user.User;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.echo.api.EchoListener;
import br.com.legion.guilds.framework.echo.packets.BroadcastMessagePacket;
import br.com.legion.guilds.framework.echo.packets.SendMessagePacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

public class EchoListeners implements EchoListener {

    @Subscribe
    public void on(SendMessagePacket packet) {
        if (packet.getUsersIds() != null) {
            for (int userId : packet.getUsersIds()) {
                User user = ApiProvider.Cache.Local.USERS.provide().get(userId);
                if (user == null) {
                    continue;
                }

                Player player = Bukkit.getPlayerExact(user.getName());
                if (player == null) {
                    continue;
                }

                player.spigot().sendMessage(packet.getComponents());
            }
        }
    }

    @Subscribe
    public void on(BroadcastMessagePacket packet) {
        if (packet.getServer() != null) {
            if (!Objects.equals(packet.getServer(), GuildsFrameworkProvider.getServerType())) {
                return;
            }
        }

        Bukkit.getOnlinePlayers().forEach(target -> target.spigot().sendMessage(packet.getComponents()));
    }
}
