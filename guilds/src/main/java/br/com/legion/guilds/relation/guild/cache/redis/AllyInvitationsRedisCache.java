package br.com.legion.guilds.relation.guild.cache.redis;

import br.com.legion.guilds.framework.GuildsFrameworkProvider;
import br.com.legion.guilds.framework.cache.redis.RedisCache;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AllyInvitationsRedisCache implements RedisCache {

    private static String getKey(int factionId) {
        return String.format("ally_invitations:%d", factionId);
    }

    public void putInvitation(int senderId, int targetId) {
        try (Jedis jedis = GuildsFrameworkProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            String key = getKey(targetId);

            pipeline.sadd(key, String.valueOf(senderId));
            pipeline.expire(key, 60 * 30);

            pipeline.sync();
        }
    }

    public Set<Integer> getInvitations(int factionId) {
        try (Jedis jedis = GuildsFrameworkProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Set<String> members = jedis.smembers(getKey(factionId));

            if (members == null || members.isEmpty()) {
                return Sets.newHashSet();
            }

            return members.stream()
                    .map(Ints::tryParse)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
    }

    public boolean hasInvite(int senderId, int targetId) {
        try (Jedis jedis = GuildsFrameworkProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.sismember(getKey(targetId), String.valueOf(senderId));
        }
    }

    public void removeInvitation(int senderId, int targetId) {
        try (Jedis jedis = GuildsFrameworkProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.srem(getKey(targetId), String.valueOf(senderId));
        }
    }
}
