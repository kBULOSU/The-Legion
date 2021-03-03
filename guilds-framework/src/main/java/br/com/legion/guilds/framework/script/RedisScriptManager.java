package br.com.legion.guilds.framework.script;

import br.com.legion.guilds.framework.GuildsFrameworkPlugin;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class RedisScriptManager {

    public static final File REDIS_FOLDER = new File(GuildsFrameworkPlugin.getInstance().getDataFolder(), "scripts/redis");

    public static final HashMap<String, String> HASHES = Maps.newHashMap();

    public static Object execute(Jedis jedis, String scriptPath, List<String> keys, List<String> args) {
        String hash = null;

        try {
            if (HASHES.containsKey(scriptPath) && jedis.scriptExists(HASHES.get(scriptPath))) {
                hash = HASHES.get(scriptPath);
            } else {
                hash = jedis.scriptLoad(FileUtils.readFileToString(new File(REDIS_FOLDER, scriptPath), Charsets.UTF_8));
                HASHES.put(scriptPath, hash);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return jedis.evalsha(hash, keys, args);
    }
}
