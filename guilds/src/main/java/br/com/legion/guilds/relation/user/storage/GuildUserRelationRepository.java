package br.com.legion.guilds.relation.user.storage;

import br.com.idea.api.shared.providers.MysqlDatabaseProvider;
import br.com.idea.api.shared.storage.repositories.MysqlRepository;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import br.com.legion.guilds.relation.user.storage.specs.*;

import java.util.Set;

public class GuildUserRelationRepository extends MysqlRepository {

    public GuildUserRelationRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);

        query(new CreateGuildUserRelationTableSpec());
    }

    public boolean update(GuildUserRelation relation) {
        return query(new InsertOrUpdateGuildUserRelationSpec(relation));
    }

    public void removeByUser(int userId) {
        query(new DeleteGuildUserRelationByUserIdSpec(userId));
    }

    public GuildUserRelation fetchByUser(int userId) {
        return query(new SelectGuildUserRelationByUserIdSpec(userId));
    }

    public Set<GuildUserRelation> fetchByGuild(int factionId) {
        return query(new SelectGuildUsersRelationsByGuildSpec(factionId));
    }

    public GuildUserRelation fetchByGuildAndRole(int factionId, GuildRole role) {
        return query(new SelectGuildUsersRelationsByGuildAndRoleSpec(factionId, role));
    }
}
