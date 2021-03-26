package br.com.legion.missions;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class MissionsPlugin extends JavaPlugin {

    @Getter
    private static MissionsPlugin instance;

    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;
    }
}
