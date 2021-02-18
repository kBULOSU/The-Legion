package br.com.legion.guilds;

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
    }

    @Override
    public void onDisable() {
        super.onDisable();

        GuildsProvider.shutdown();
    }
}
