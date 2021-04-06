package br.com.legion.economy;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.providers.LocalCacheProvider;
import br.com.idea.api.shared.providers.MysqlRepositoryProvider;
import br.com.legion.economy.local.EconomyLocalCache;
import br.com.legion.economy.storage.EconomyRepository;
import br.com.legion.economy.transactions.cache.local.TransactionsLocalCache;
import br.com.legion.economy.transactions.storage.TransactionsRepository;

public class EconomyProvider {

    public static void prepare() {
        Repositories.ECONOMY.prepare();
        Repositories.TRANSACTIONS.prepare();

        Cache.Local.ECONOMY.prepare();
    }

    public static class Repositories {

        public static final MysqlRepositoryProvider<EconomyRepository> ECONOMY =
                new MysqlRepositoryProvider<>(
                        () -> ApiProvider.Database.MySQL.MYSQL_MAIN,
                        EconomyRepository.class
                );

        public static final MysqlRepositoryProvider<TransactionsRepository> TRANSACTIONS =
                new MysqlRepositoryProvider<>(
                        () -> ApiProvider.Database.MySQL.MYSQL_MAIN,
                        TransactionsRepository.class
                );

    }

    public static class Cache {

        public static class Local {

            public static final LocalCacheProvider<EconomyLocalCache> ECONOMY =
                    new LocalCacheProvider<>(new EconomyLocalCache());

            public static final LocalCacheProvider<TransactionsLocalCache> TRANSACTIONS =
                    new LocalCacheProvider<>(new TransactionsLocalCache());

        }
    }
}
