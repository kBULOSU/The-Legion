package br.com.legion.guilds.relation.user.cache.local;

import br.com.idea.api.shared.cache.LocalCache;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GuildUsersRelationsLocalCache implements LocalCache {

    private final LoadingCache<Integer, Set<GuildUserRelation>> CACHE_BY_GUILD = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build((guildId) -> GuildsProvider.Repositories.USERS_RELATIONS.provide().fetchByGuild(guildId));

    private final LoadingCache<GuildAndRoleCacheLookup, GuildUserRelation> CACHE_BY_GUILD_AND_ROLE = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build((lookup) -> GuildsProvider.Repositories.USERS_RELATIONS.provide().fetchByGuildAndRole(lookup.guildId, lookup.role));

    private final LoadingCache<Integer, GuildUserRelation> CACHE_BY_USERS = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build((userId) -> GuildsProvider.Repositories.USERS_RELATIONS.provide().fetchByUser(userId));

    /*

     */

    public GuildUserRelation getByUser(int id) {
        return CACHE_BY_USERS.get(id);
    }

    public Set<GuildUserRelation> getByClan(int factionId) {
        return CACHE_BY_GUILD.get(factionId);
    }

    public GuildUserRelation getByClanAndRole(int factionId, GuildRole role) {
        return CACHE_BY_GUILD_AND_ROLE.get(new GuildAndRoleCacheLookup(factionId, role));
    }

    /*


     */

    public void invalidateUser(int userId) {
        CACHE_BY_USERS.invalidate(userId);
    }

    public void invalidateClan(int factionId) {
        CACHE_BY_GUILD.invalidate(factionId);

        for (GuildRole role : GuildRole.values()) {
            CACHE_BY_GUILD_AND_ROLE.invalidate(new GuildAndRoleCacheLookup(factionId, role));
        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class GuildAndRoleCacheLookup {

        private final int guildId;

        private final GuildRole role;

    }
}
