package br.com.legion.essentials.listeners;

import br.com.idea.api.spigot.misc.message.Message;
import br.com.idea.api.spigot.misc.utils.LocationUtils;
import br.com.legion.essentials.EssentialsPlugin;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.Events;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;

public class ServerListeners {

    private final ImmutableSet<CreatureSpawnEvent.SpawnReason> SPAWN_REASONS = ImmutableSet.<CreatureSpawnEvent.SpawnReason>builder()
            .add(CreatureSpawnEvent.SpawnReason.CUSTOM)
            .add(CreatureSpawnEvent.SpawnReason.SPAWNER)
            .add(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
            .add(CreatureSpawnEvent.SpawnReason.EGG)
            .build();

    public ServerListeners(EssentialsPlugin plugin) {
        Events.subscribe(PlayerJoinEvent.class, EventPriority.LOWEST)
                .handler(event -> {
                    event.setJoinMessage(null);

                    Player player = event.getPlayer();
                })
                .bindWith(plugin);

        Events.subscribe(PlayerQuitEvent.class, EventPriority.LOWEST)
                .handler(event -> event.setQuitMessage(null))
                .bindWith(plugin);

        Events.subscribe(CreatureSpawnEvent.class, EventPriority.LOWEST)
                .filter(event -> !SPAWN_REASONS.contains(event.getSpawnReason()))
                .handler(event -> {

                    event.setCancelled(true);
                    event.getEntity().remove();

                })
                .bindWith(plugin);

        Events.subscribe(FoodLevelChangeEvent.class, EventPriority.LOWEST)
                .filter(event -> event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel())
                .handler(event -> event.setCancelled(true))
                .bindWith(plugin);

        Events.subscribe(ExplosionPrimeEvent.class)
                .filter(event -> event.getEntityType() == EntityType.WITHER || event.getEntityType() == EntityType.WITHER_SKULL)
                .handler(event -> event.setCancelled(true))
                .bindWith(plugin);

        Events.subscribe(WeatherChangeEvent.class, EventPriority.HIGHEST)
                .handler(event -> {
                    if (event.toWeatherState()) {
                        event.setCancelled(true);
                    }
                })
                .bindWith(plugin);

        Events.subscribe(SignChangeEvent.class)
                .handler(event -> {
                    if (event.getPlayer().hasPermission("grupo.vip")) {
                        for (int i = 0; i < event.getLines().length; i++) {
                            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
                        }
                    }

                })
                .bindWith(plugin);

        Events.subscribe(PlayerTeleportEvent.class, EventPriority.HIGH)
                .filter(event -> LocationUtils.isOutsideOfBorder(event.getTo()))
                .handler(event -> {
                    event.setCancelled(true);
                    event.setTo(event.getFrom());
                })
                .bindWith(plugin);

        Events.subscribe(PlayerDeathEvent.class, EventPriority.HIGHEST)
                .handler(event -> {
                    event.setDeathMessage(null);

                    Player player = event.getEntity();
                    if (player.hasPermission("grupo.vip")) {
                        event.setKeepLevel(true);
                    }
                })
                .bindWith(plugin);

        Events.merge(Vehicle.class)
                .bindEvent(VehicleCreateEvent.class, VehicleCreateEvent::getVehicle)
                .bindEvent(VehicleEnterEvent.class, VehicleEnterEvent::getVehicle)
                .handler(Entity::remove)
                .bindWith(plugin);

        Events.subscribe(PlayerBedEnterEvent.class, EventPriority.HIGHEST)
                .handler(event -> {
                    event.setCancelled(true);
                    event.getBed().setType(Material.AIR);
                })
                .bindWith(plugin);

        Events.subscribe(PortalCreateEvent.class, EventPriority.HIGHEST)
                .handler(portalCreateEvent -> portalCreateEvent.setCancelled(true))
                .bindWith(plugin);

        Events.subscribe(PlayerPortalEvent.class, EventPriority.HIGHEST)
                .handler(portalEnterEvent -> portalEnterEvent.setCancelled(true))
                .bindWith(plugin);

        Events.merge(Block.class)
                .bindEvent(BlockPistonRetractEvent.class, BlockPistonRetractEvent::getBlock)
                .bindEvent(BlockPistonExtendEvent.class, BlockPistonExtendEvent::getBlock)
                .handler(block -> block.setType(Material.AIR))
                .bindWith(plugin);

        Events.subscribe(InventoryOpenEvent.class, EventPriority.HIGHEST)
                .filter(event -> event.getInventory().getType() == InventoryType.HOPPER)
                .handler(event -> event.setCancelled(true))
                .bindWith(plugin);

        Events.subscribe(PlayerInteractAtEntityEvent.class, EventPriority.LOWEST)
                .handler(event -> {
                    ItemStack itemInHand = event.getPlayer().getItemInHand();
                    if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                        return;
                    }

                    if (itemInHand.getType() == Material.NAME_TAG) {
                        event.setCancelled(true);
                    }
                })
                .bindWith(plugin);

        Events.subscribe(PlayerInteractEvent.class, EventPriority.HIGH)
                .filter(PlayerInteractEvent::hasItem)
                .filter(event -> event.getItem().getType() == Material.ENDER_PEARL)
                .handler(event -> {
                    Player player = event.getPlayer();

                    ItemStack item = player.getItemInHand();

                    if (item == null) {
                        return;
                    }

                    Material type = item.getType();
                    if (type == Material.BANNER) {
                        if (!event.isCancelled()) {
                            event.setCancelled(!player.isOp());

                            if (event.isCancelled()) {
                                Message.ERROR.send(player, "&cNão é possível utilizar banners. :(");
                            }
                        }
                    } else if (type == Material.BOAT) {
                        if (!event.isCancelled()) {
                            event.setCancelled(!player.isOp());
                        }
                    }
                })
                .bindWith(plugin);
    }

}
