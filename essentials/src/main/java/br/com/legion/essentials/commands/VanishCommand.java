package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import br.com.legion.essentials.EssentialsPlugin;
import com.google.common.collect.Sets;
import me.lucko.helper.Events;
import me.lucko.helper.utils.Players;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

public class VanishCommand extends CustomCommand {

    private final Set<String> hidden = Sets.newHashSet();

    public VanishCommand(EssentialsPlugin plugin) {
        super("vanish", CommandRestriction.IN_GAME, "v");

        Events.subscribe(PlayerQuitEvent.class)
                .filter(event -> hidden.contains(event.getPlayer().getName()))
                .handler(event -> {
                    hidden.remove(event.getPlayer().getName());

                    for (Player player : Players.all()) {
                        player.showPlayer(event.getPlayer());
                    }
                })
                .bindWith(plugin);

        setPermission("command.vanish");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (hidden.contains(player.getName())) {
            for (Player onlinePlayer : Players.all()) {
                onlinePlayer.showPlayer(player);
            }

            hidden.remove(sender.getName());
            return;
        }

        for (Player onlinePlayer : Players.all()) {
            onlinePlayer.hidePlayer(player);
        }

        hidden.add(sender.getName());

        Message.INFO.send(sender, "Modo invísivel alterado com sucesso.");
    }
}
