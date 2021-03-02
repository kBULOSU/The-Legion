package br.com.legion.guilds.relation.guild.storage;

import br.com.idea.api.shared.providers.MysqlDatabaseProvider;
import br.com.idea.api.shared.storage.repositories.MysqlRepository;
import br.com.legion.guilds.relation.guild.GuildRelation;
import br.com.legion.guilds.relation.guild.storage.specs.*;

import java.util.Set;

public class GuildRelationsRepository extends MysqlRepository {

    public GuildRelationsRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);

        query(new CreateGuildRelationTableSpec());
    }

    public GuildRelation fetchByRelation(int guildId1, int guildId2) {
        return query(new SelectGuildRelationSpec(guildId1, guildId2));
    }

    public Set<Integer> fetchRelationships(int guildId, GuildRelation type) {
        return query(new SelectGuildRelationshipsSpec(guildId, type));
    }

    public void delete(int guildId1, int guildId2) {
        query(new DeleteGuildRelationSpec(guildId1, guildId2));
    }

    public void insert(int guildId1, int guildId2, GuildRelation relation) {
        query(new InsertGuildRelationSpec(guildId1, guildId2, relation));
    }
}
