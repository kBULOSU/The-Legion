package br.com.legion.guilds.framework;

import br.com.idea.api.shared.misc.utils.Printer;
import br.com.legion.guilds.framework.echo.api.DebugPacket;
import br.com.legion.guilds.framework.echo.api.Echo;
import br.com.legion.guilds.framework.echo.api.EchoSubscriber;
import br.com.legion.guilds.framework.echo.listeners.EchoListeners;
import br.com.legion.guilds.framework.providers.RedisProvider;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;
import java.util.Map;

public class GuildsFrameworkPlugin extends JavaPlugin {

    @Getter
    private static GuildsFrameworkPlugin instance;

    @Override
    public void onEnable() {
        super.onEnable();

        saveDefaultConfig();

        instance = this;

        InetSocketAddress address = new InetSocketAddress(
                getConfig().getString("redis.redis-host"),
                getConfig().getInt("redis.redis-port")
        );

        String auth = getConfig().getString("redis.redis-auth");

        GuildsFrameworkProvider.prepare(
                new RedisProvider(
                        address,
                        auth
                ),
                new RedisProvider(
                        address,
                        auth
                )
        );

        /**
         * Registrando eventos do Echo
         */
        Printer.INFO.print("Initializing echo service");
        Echo echo = GuildsFrameworkProvider.Redis.ECHO.provide();

        Map<String, Timing> timings = Maps.newConcurrentMap();

        EchoSubscriber echoSubscriber = echo.subscribe((packet, runnable) -> {
            Class clazz = packet.getClass();
            boolean debug = clazz.getAnnotation(DebugPacket.class) != null;

            if (debug) {
                Printer.INFO.print(String.format("Executor - %s", clazz.getSimpleName()));
            }

            Runnable executor = () -> {
                Timing timing = timings.getOrDefault(
                        packet.getClass().getName(),
                        Timings.of(
                                this,
                                "EchoPacket: " + packet.getClass().getName() + " (" + packet.getClass().getSimpleName() + ")"
                        )
                );

                timing.startTiming();
                if (debug) {
                    Printer.INFO.print(String.format("Runnable run - %s", clazz.getSimpleName()));
                }
                runnable.run();
                timing.stopTiming();
            };

            if (this.isEnabled()) {
                if (getServer().isPrimaryThread()) {
                    if (debug) {
                        Printer.INFO.print(String.format("Primary Thread run - %s", clazz.getSimpleName()));
                    }
                    executor.run();
                } else {
                    Bukkit.getScheduler().runTask(this, () -> {
                        if (debug) {
                            Printer.INFO.print(String.format("RunTask run - %s", clazz.getSimpleName()));
                        }

                        executor.run();
                    });
                }
            }

        });

        echo.setDefaultSubscriber(echoSubscriber);
        echo.registerListener(new EchoListeners());
    }

    @Override
    public void onDisable() {
        super.onDisable();

        GuildsFrameworkProvider.shut();
    }
}
