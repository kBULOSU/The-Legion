package br.com.legion.guilds.relation.user.cache.redis;

import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.cache.redis.RedisCache;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GuildInvitationsRedisCache implements RedisCache {

    private static String getKey(int userId) {
        return String.format("factions_invitations:%d", userId);
    }

    public void putInvitation(int userId, int factionId) {
        try (Jedis jedis = GuildsFrameworkProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            String key = getKey(userId);

            pipeline.sadd(key, String.valueOf(factionId));
            pipeline.expire(key, 60 * 30);

            pipeline.sync();
        }
    }

    public Set<Integer> getInvitations(int userId) {
        try (Jedis jedis = GuildsFrameworkProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Set<String> members = jedis.smembers(getKey(userId));

            if (members == null || members.isEmpty()) {
                return Sets.newHashSet();
            }

            return members.stream()
                    .map(Ints::tryParse)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
    }

    public boolean hasInvite(int userId, int factionId) {
        try (Jedis jedis = GuildsFrameworkProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.sismember(getKey(userId), String.valueOf(factionId));
        }
    }

    public void clearInvitations(int userId) {
        try (Jedis jedis = GuildsFrameworkProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.del(getKey(userId));
        }
    }
}
