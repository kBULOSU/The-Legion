package br.com.legion.essentials.commands;

import br.com.idea.api.shared.commands.CommandRestriction;
import br.com.idea.api.shared.misc.utils.NumberUtils;
import br.com.idea.api.shared.misc.utils.TimeCode;
import br.com.idea.api.spigot.commands.CustomCommand;
import br.com.idea.api.spigot.misc.message.Message;
import com.sun.management.OperatingSystemMXBean;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;

public class LagCommand extends CustomCommand {

    private final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    public LagCommand() {
        super("lag", CommandRestriction.CONSOLE_AND_IN_GAME);

        setPermission("command.lag");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        double[] tps = org.bukkit.Bukkit.spigot().getTPS();
        String[] tpsAvg = new String[tps.length];

        for (int i = 0; i < tps.length; i++) {
            tpsAvg[i] = format(tps[i]);
        }

        long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();

        double tickLoad = MathHelper.a(MinecraftServer.getServer().h) * 1.0E-6D;

        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory() / 1024L / 1024L;
        long usedMemory = runtime.maxMemory() - runtime.freeMemory() / 1024L / 1024L;

        Message.EMPTY.send(sender, "");
        Message.INFO.send(sender, "TPS (1m, 5m, 15m): " + String.join(", ", tpsAvg));
        Message.INFO.send(sender, "Uptime: &f" + TimeCode.toText(jvmUpTime, 5));
        Message.INFO.send(sender, "Load AVG: &f" + tickLoad);
        Message.INFO.send(sender, "CPU: &f" + NumberUtils.format(osBean.getSystemCpuLoad() * 100) + "%");
        Message.INFO.send(sender, "Memória: &f" + maxMemory + "/" + usedMemory);
        Message.EMPTY.send(sender, "");
    }

    private String format(double tps) {
        return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED)
                + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }
}
