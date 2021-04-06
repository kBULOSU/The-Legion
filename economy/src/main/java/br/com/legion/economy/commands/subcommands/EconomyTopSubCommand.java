package br.com.legion.economy.commands.subcommands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.legion.economy.EconomyProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EconomyTopSubCommand extends CustomCommand {

    public EconomyTopSubCommand() {
        super("top", CommandRestriction.IN_GAME, "rank");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        StringBuilder message = new StringBuilder();

        message.append("\n")
                .append(ChatColor.YELLOW)
                .append("Top 10 jogadores com mais ")
                .append("Coins")
                .append("\n \n");

        AtomicInteger count = new AtomicInteger(1);

        for (Map.Entry<String, Double> entry : EconomyProvider.Cache.Local.ECONOMY.provide().getRank(10).entrySet()) {
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
