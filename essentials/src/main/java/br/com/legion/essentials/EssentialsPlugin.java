package br.com.legion.essentials;

import br.com.legion.essentials.config.EssentialsConfig;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.plugin.ap.PluginDependency;

@Plugin(
        name = "L-Essentials",
        version = "0.0.1",
        authors = "yiatzz",
        depends = @PluginDependency("helper")
)
public class EssentialsPlugin extends ExtendedJavaPlugin {

    @Getter
    private static EssentialsPlugin instance;

    @Getter
    private static EssentialsConfig config;

    @Override
    protected void enable() {
        super.onEnable();

        instance = this;
    }
}
