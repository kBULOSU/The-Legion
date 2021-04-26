package br.com.legion.essentials.config;

import com.focamacho.sealconfig.SealConfig;

public class EssentialsConfig {

    private static SealConfig config;

    public static void loadConfig() {
        config = new SealConfig();


    }

    public static void reloadConfig() {
        config.reload();
    }
}
