package br.com.legion.essentials;

import br.com.legion.essentials.config.EssentialsConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class EssentialsPlugin extends JavaPlugin {

    @Getter
    private static EssentialsPlugin instance;

    @Getter
    private static EssentialsConfig config;

    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;
    }
}
