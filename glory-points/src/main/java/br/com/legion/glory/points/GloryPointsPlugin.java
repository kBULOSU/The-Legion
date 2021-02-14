package br.com.legion.glory.points;

import br.com.idea.api.spigot.commands.CommandRegistry;
import br.com.legion.glory.points.commands.GloryPointsCommand;
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

        CommandRegistry.registerCommand(new GloryPointsCommand());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
