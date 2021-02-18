package br.com.legion.guilds;

import br.com.idea.api.shared.ApiProvider;
import br.com.idea.api.shared.contracts.Provider;
import br.com.idea.api.shared.providers.LocalCacheProvider;
import br.com.idea.api.shared.providers.MysqlRepositoryProvider;
import br.com.legion.guilds.cache.local.GuildLocalCache;
import br.com.legion.guilds.relation.guild.cache.local.GuildRelationsLocalCache;
import br.com.legion.guilds.relation.guild.storage.GuildRelationsRepository;
import br.com.legion.guilds.relation.user.cache.local.GuildUsersRelationsLocalCache;
import br.com.legion.guilds.relation.user.storage.GuildUserRelationRepository;
import br.com.legion.guilds.storage.GuildsRepository;
import br.com.legion.guilds.user.cache.local.GuildUserLocalCache;
import com.google.common.collect.Lists;

import java.util.LinkedList;

public class GuildsProvider {

    public static final LinkedList<Provider<?>> PROVIDERS = Lists.newLinkedList();

    static {
        PROVIDERS.add(Repositories.GUILDS);
        PROVIDERS.add(Repositories.USERS_RELATIONS);
        PROVIDERS.add(Repositories.GUILDS_RELATIONS);

        PROVIDERS.add(Cache.Local.GUILDS);
        PROVIDERS.add(Cache.Local.USERS);
        PROVIDERS.add(Cache.Local.USERS_RELATIONS);
        PROVIDERS.add(Cache.Local.GUILDS_RELATIONS);
    }

    public static void prepare() {
        PROVIDERS.forEach(Provider::prepare);
    }

    public static void shutdown() {
        PROVIDERS.forEach(Provider::shutdown);
    }

    public static class Repositories {

        public static final MysqlRepositoryProvider<GuildsRepository> GUILDS =
                new MysqlRepositoryProvider<>(
                        () -> ApiProvider.Database.MySQL.MYSQL_MAIN,
                        GuildsRepository.class
                );

        public static final MysqlRepositoryProvider<GuildUserRelationRepository> USERS_RELATIONS =
                new MysqlRepositoryProvider<>(
                        () -> ApiProvider.Database.MySQL.MYSQL_MAIN,
                        GuildUserRelationRepository.class
                );

        public static final MysqlRepositoryProvider<GuildRelationsRepository> GUILDS_RELATIONS =
                new MysqlRepositoryProvider<>(
                        () -> ApiProvider.Database.MySQL.MYSQL_MAIN,
                        GuildRelationsRepository.class
                );
    }

    public static class Cache {

        public static class Local {

            public static final LocalCacheProvider<GuildLocalCache> GUILDS = new LocalCacheProvider<>(
                    new GuildLocalCache()
            );

            public static final LocalCacheProvider<GuildUserLocalCache> USERS = new LocalCacheProvider<>(
                    new GuildUserLocalCache()
            );

            public static final LocalCacheProvider<GuildUsersRelationsLocalCache> USERS_RELATIONS = new LocalCacheProvider<>(
                    new GuildUsersRelationsLocalCache()
            );

            public static final LocalCacheProvider<GuildRelationsLocalCache> GUILDS_RELATIONS = new LocalCacheProvider<>(
                    new GuildRelationsLocalCache()
            );
        }

    }
}
