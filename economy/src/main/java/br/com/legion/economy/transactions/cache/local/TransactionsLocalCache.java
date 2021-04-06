package br.com.legion.economy.transactions.cache.local;

import br.com.idea.api.shared.cache.LocalCache;
import br.com.legion.economy.EconomyProvider;
import br.com.legion.economy.transactions.Transaction;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TransactionsLocalCache implements LocalCache {

    private final LoadingCache<Integer, List<Transaction>> CACHE_BY_NAME = Caffeine.newBuilder()
            .expireAfterWrite(10L, TimeUnit.SECONDS)
            .build(id -> EconomyProvider.Repositories.TRANSACTIONS.provide().fetch(id));

    public List<Transaction> get(Integer id) {
        return CACHE_BY_NAME.get(id);
    }

    public void invalidate(Integer id) {
        CACHE_BY_NAME.invalidate(id);
    }

}
