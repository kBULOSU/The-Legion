package br.com.legion.essentials;

import com.google.common.collect.Maps;
import org.bukkit.GameMode;

import java.util.EnumMap;

public class EssentialsConstants {

    public static final EnumMap<GameMode, String> ALLOW_GAMEMODE = Maps.newEnumMap(GameMode.class);

    static {
        ALLOW_GAMEMODE.put(GameMode.CREATIVE, "gamemode.creative");
        ALLOW_GAMEMODE.put(GameMode.SPECTATOR, "gamemode.spec");
        ALLOW_GAMEMODE.put(GameMode.ADVENTURE, "gamemode.adventure");
    }

}
