package br.com.legion.guilds.framework;

import br.com.legion.guilds.framework.providers.EchoProvider;
import br.com.legion.guilds.framework.providers.RedisProvider;
import br.com.legion.guilds.framework.server.ServerType;

public class GuildsFrameworkProvider {

    public static void prepare(RedisProvider redisMain, RedisProvider redisEcho) {
        Redis.REDIS_MAIN = redisMain;
        Redis.REDIS_ECHO = redisEcho;

        Redis.REDIS_MAIN.prepare();
        Redis.REDIS_ECHO.prepare();
        Redis.ECHO.prepare();
    }

    public static void shut() {
        Redis.REDIS_MAIN.shutdown();
        Redis.REDIS_ECHO.shutdown();
        Redis.ECHO.shutdown();
    }

    public static class Redis {

        public static RedisProvider REDIS_MAIN;

        public static RedisProvider REDIS_ECHO;

        public final static EchoProvider ECHO = new EchoProvider(() -> Redis.REDIS_ECHO);
    }


    public static ServerType getServerType() {
        return GuildsFrameworkConstants.SERVER_TYPE;
    }

}
