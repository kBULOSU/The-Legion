package br.com.legion.glory.points.commands.subcommands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.legion.glory.points.GloryPointsAPI;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GloryPointsTopSubCommand extends CustomCommand {

    private final Map<String, Double> top = Maps.newLinkedHashMap();

    private Long cooldown;

    public GloryPointsTopSubCommand() {
        super("top", CommandRestriction.IN_GAME, "rank");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (this.cooldown == null || this.cooldown < System.currentTimeMillis()) {
            this.top.clear();
            this.top.putAll(GloryPointsAPI.getTop(10));

            this.cooldown = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
        }

        StringBuilder message = new StringBuilder();

        message.append("\n")
                .append(ChatColor.YELLOW)
                .append("Top 10 jogadores com mais ")
                .append("Pontos de Glória")
                .append("\n \n");

        AtomicInteger count = new AtomicInteger(1);

        for (Map.Entry<String, Double> entry : this.top.entrySet()) {
            String name = entry.getKey();
            Double points = entry.getValue();

            message.append("  ")
                    .append(ChatColor.WHITE)
                    .append(count.getAndIncrement())
                    .append("° ")
                    .append(ChatColor.GRAY)
                    .append(name)
                    .append(":")
                    .append("  §f")
                    .append(ChatColor.WHITE)
                    .append(NumberUtils.format(points))
                    .append(".")
                    .append("\n");
        }

        message.append("\n");

        sender.sendMessage(message.toString());
    }
}
