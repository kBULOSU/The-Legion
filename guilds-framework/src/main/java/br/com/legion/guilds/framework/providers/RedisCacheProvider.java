package br.com.legion.guilds.framework.providers;

import br.com.idea.api.shared.contracts.Provider;
import br.com.legion.guilds.framework.cache.redis.RedisCache;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedisCacheProvider<T extends RedisCache> implements Provider<T> {

    private final T cache;

    @Override
    public void prepare() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public T provide() {
        return cache;
    }
}
