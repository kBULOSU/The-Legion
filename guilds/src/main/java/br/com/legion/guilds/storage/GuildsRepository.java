package br.com.legion.guilds.storage;


import br.com.idea.api.shared.providers.MysqlDatabaseProvider;
import br.com.idea.api.shared.storage.repositories.MysqlRepository;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.storage.specs.*;

public class GuildsRepository extends MysqlRepository {

    public GuildsRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);

        query(new CreateGuildsTableSpec());
    }

    public Guild create(String tag, String name, int level, int maxMembers, double gloryPoints) {
        return query(new InsertGuildSpec(tag, name, level, maxMembers, gloryPoints));
    }

    public boolean delete(int id) {
        return query(new DeleteGuildSpec(id));
    }

    public Guild fetchByTag(String tag) {
        return query(new SelectGuildByTagSpec(tag));
    }

    public Guild fetchById(Integer id) {
        return query(new SelectGuildByIdSpec(id));
    }

    public Guild fetchByName(String name) {
        return query(new SelectGuildByNameSpec(name));
    }
}
