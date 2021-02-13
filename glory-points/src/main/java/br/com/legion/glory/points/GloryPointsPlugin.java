package br.com.legion.glory.points;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class GloryPointsPlugin extends JavaPlugin {

    @Getter
    private static GloryPointsPlugin instance;

    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;

        GloryPointsProvider.prepare();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
