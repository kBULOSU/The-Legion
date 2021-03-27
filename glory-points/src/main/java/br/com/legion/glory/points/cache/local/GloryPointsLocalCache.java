package br.com.legion.glory.points.cache.local;

import br.com.idea.api.shared.cache.LocalCache;
import br.com.legion.glory.points.GloryPointsProvider;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GloryPointsLocalCache implements LocalCache {

    private final LoadingCache<String, Double> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(5L, TimeUnit.SECONDS)
            .build(name -> GloryPointsProvider.Repositories.GLORY_POINTS.provide().fetch(name));

    public Double get(String name) {
        return CACHE.get(name);
    }

    public void invalidate(String name) {
        CACHE.invalidate(name);
    }

    private Long cooldown;
    private final Map<String, Double> ranking = Maps.newLinkedHashMap();

    public Map<String, Double> getRank(int index) {
        if (cooldown == null || cooldown < System.currentTimeMillis()) {
            cooldown = System.currentTimeMillis() + 60000L;
            ranking.clear();
            ranking.putAll(GloryPointsProvider.Repositories.GLORY_POINTS.provide().fetchTop(index));
        }

        return ranking;
    }
}
