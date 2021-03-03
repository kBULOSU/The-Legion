package br.com.legion.guilds;

import br.com.idea.api.spigot.commands.CommandRegistry;
import br.com.legion.guilds.commands.AllyChatCommand;
import br.com.legion.guilds.commands.GuildChatCommand;
import br.com.legion.guilds.commands.GuildCommand;
import br.com.legion.guilds.echo.listeners.GuildEchoListener;
import br.com.legion.guilds.echo.listeners.GuildUserEchoListener;
import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class GuildsPlugin extends JavaPlugin {

    @Getter
    private static GuildsPlugin instance;

    @Override
    public void onLoad() {
        super.onLoad();

        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        GuildsProvider.prepare();

        GuildsFrameworkProvider.Redis.ECHO.provide().registerListener(new GuildEchoListener());
        GuildsFrameworkProvider.Redis.ECHO.provide().registerListener(new GuildUserEchoListener());

        CommandRegistry.registerCommand(
                new GuildCommand(),
                new AllyChatCommand(),
                new GuildChatCommand()
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();

        GuildsProvider.shutdown();
    }
}
