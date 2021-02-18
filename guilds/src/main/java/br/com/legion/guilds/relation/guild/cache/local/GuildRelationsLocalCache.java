package br.com.legion.guilds.relation.guild.cache.local;

import br.com.idea.api.shared.cache.LocalCache;
import br.com.legion.guilds.GuildsProvider;
import br.com.legion.guilds.relation.guild.GuildRelation;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GuildRelationsLocalCache implements LocalCache {

    private final LoadingCache<RelationCacheLookup, GuildRelation> CACHE_BY_RELATION = Caffeine
            .newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(lookup -> {
                return GuildsProvider.Repositories.GUILDS_RELATIONS.provide().fetchByRelation(lookup.min, lookup.max);
            });

    private final LoadingCache<RelationshipCacheLookup, Set<Integer>> CACHE_BY_RELATIONSHIPS = Caffeine
            .newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(lookup -> {
                return GuildsProvider.Repositories.GUILDS_RELATIONS.provide().fetchRelationships(lookup.guildId, lookup.relation);
            });

    public GuildRelation getRelation(int guildId1, int guildId2) {
        return CACHE_BY_RELATION.get(new RelationCacheLookup(Math.min(guildId1, guildId2), Math.max(guildId1, guildId2)));
    }

    public Set<Integer> getRelationships(int guildId, GuildRelation relation) {
        return CACHE_BY_RELATIONSHIPS.get(new RelationshipCacheLookup(guildId, relation));
    }

    /*

     */

    public void invalidate(int guildId1, int guildId2) {
        CACHE_BY_RELATION.invalidate(new RelationCacheLookup(Math.min(guildId1, guildId2), Math.max(guildId1, guildId2)));
    }


    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class RelationCacheLookup {

        private final int min, max;

    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class RelationshipCacheLookup {

        private final int guildId;
        private final GuildRelation relation;

    }
}
