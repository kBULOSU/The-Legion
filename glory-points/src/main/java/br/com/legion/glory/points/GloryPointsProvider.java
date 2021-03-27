package br.com.legion.glory.points;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.providers.LocalCacheProvider;
import br.com.idea.api.shared.providers.MysqlRepositoryProvider;
import br.com.legion.glory.points.cache.local.GloryPointsLocalCache;
import br.com.legion.glory.points.storage.GloryPointsRepository;
import br.com.legion.glory.points.transactions.storage.TransactionsRepository;

public class GloryPointsProvider {

    public static void prepare() {
        Repositories.GLORY_POINTS.prepare();
        Repositories.TRANSACTIONS.prepare();

        Cache.Local.GLORY_POINTS.prepare();
    }

    public static class Repositories {

        public static final MysqlRepositoryProvider<GloryPointsRepository> GLORY_POINTS =
                new MysqlRepositoryProvider<>(
                        () -> ApiProvider.Database.MySQL.MYSQL_MAIN,
                        GloryPointsRepository.class
                );

        public static final MysqlRepositoryProvider<TransactionsRepository> TRANSACTIONS =
                new MysqlRepositoryProvider<>(
                        () -> ApiProvider.Database.MySQL.MYSQL_MAIN,
                        TransactionsRepository.class
                );

    }

    public static class Cache {

        public static class Local {

            public static final LocalCacheProvider<GloryPointsLocalCache> GLORY_POINTS =
                    new LocalCacheProvider<>(new GloryPointsLocalCache());

        }
    }
}
