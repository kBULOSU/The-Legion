package br.com.legion.economy;

import br.com.idea.api.spigot.commands.CommandRegistry;
import br.com.legion.economy.commands.EconomyCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyPlugin extends JavaPlugin {

    @Getter
    private static EconomyPlugin instance;

    @Override
    public void onEnable() {
        super.onEnable();

        instance = this;

        EconomyProvider.prepare();

        CommandRegistry.registerCommand(new EconomyCommand());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
