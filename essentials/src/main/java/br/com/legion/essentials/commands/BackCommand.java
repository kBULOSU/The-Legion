package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.essentials.EssentialsPlugin;
import com.google.common.collect.Maps;
import me.lucko.helper.Events;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Map;

public class BackCommand extends CustomCommand {

    private final Map<String, Location> LOCATIONS = Maps.newHashMap();

    public BackCommand(EssentialsPlugin plugin) {
        super("back", CommandRestriction.IN_GAME);

        Events.subscribe(PlayerTeleportEvent.class, EventPriority.HIGHEST)
                .handler(event -> LOCATIONS.put(event.getPlayer().getName(), event.getFrom()))
                .bindWith(plugin);

        Events.subscribe(PlayerDeathEvent.class, EventPriority.HIGHEST)
                .filter(event -> event.getEntity().isOnline())
                .handler(event -> LOCATIONS.put(event.getEntity().getName(), event.getEntity().getLocation()))
                .bindWith(plugin);

        Events.subscribe(PlayerQuitEvent.class)
                .handler(event -> LOCATIONS.remove(event.getPlayer().getName()))
                .bindWith(plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Location location = LOCATIONS.get(player.getName());
        if (location == null) {
            Message.ERROR.send(player, "Você não possui um local para retornar.");
            return;
        }

        player.teleport(location);

        Message.SUCCESS.send(player, "Retornado ao local anterior com sucesso.");
    }
}
