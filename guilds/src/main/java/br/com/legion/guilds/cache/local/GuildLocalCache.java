package br.com.legion.guilds.cache.local;

import br.com.idea.api.shared.cache.LocalCache;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsProvider;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public class GuildLocalCache implements LocalCache {

    private final LoadingCache<String, Guild> CACHE_BY_TAG = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(tag -> GuildsProvider.Repositories.GUILDS.provide().fetchByTag(tag));

    private final LoadingCache<Integer, Guild> CACHE_BY_ID = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build(id -> GuildsProvider.Repositories.GUILDS.provide().fetchById(id));

    public Guild getById(@NonNull Integer id) {
        return CACHE_BY_ID.get(id);
    }

    public Guild getByTag(@NonNull String tag) {
        return CACHE_BY_TAG.get(tag.toLowerCase());
    }

    public void invalidateId(int factionId) {
        Guild guild = CACHE_BY_ID.getIfPresent(factionId);

        if (guild != null) {
            CACHE_BY_TAG.invalidate(guild.getTag().toLowerCase());
        }

        CACHE_BY_ID.invalidate(factionId);
    }

    public void invalidateTag(String tag) {
        Guild guild = CACHE_BY_TAG.getIfPresent(tag.toLowerCase());

        if (guild != null) {
            CACHE_BY_ID.invalidate(guild.getId());
        }

        CACHE_BY_TAG.invalidate(tag.toLowerCase());
    }

    public ImmutableSet<Guild> fetchAll() {
        return ImmutableSet.copyOf(this.CACHE_BY_ID.asMap().values());
    }
}
