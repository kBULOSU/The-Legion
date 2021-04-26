package br.com.legion.essentials.systems.tpa;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TPASystem {

    private static final Cache<String, Set<String>> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(1L, TimeUnit.MINUTES)
            .build();

    public static boolean hasRequest(String to, String from) {
        Set<String> requests = CACHE.getIfPresent(to);
        if (requests == null) {
            return false;
        }

        return requests.contains(from);
    }

    public static void request(String to, String from) {
        Set<String> toRequests = CACHE.getIfPresent(to);
        if (toRequests == null) {
            toRequests = Sets.newHashSet();

            toRequests.add(from);

            CACHE.put(to, toRequests);
            return;
        }

        toRequests.add(from);
    }

    public static void remove(String to, String from) {
        Objects.requireNonNull(CACHE.getIfPresent(to)).remove(from);
    }
}
