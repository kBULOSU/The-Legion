package br.com.legion.guilds.user.cache.local;

import br.com.idea.api.shared.cache.LocalCache;
import br.com.legion.guilds.user.GuildUser;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class GuildUserLocalCache implements LocalCache {

    private final Cache<Integer, GuildUser> CACHE_BY_ID = Caffeine.newBuilder()
            .build();

    public void put(GuildUser user) {
        CACHE_BY_ID.put(user.getUserId(), user);
    }

    public GuildUser get(Integer id) {
        return CACHE_BY_ID.getIfPresent(id);
    }

    public void remove(Integer id) {
        CACHE_BY_ID.invalidate(id);
    }


}
