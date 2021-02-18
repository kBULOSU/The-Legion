package br.com.legion.guilds.relation.guild.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.UpdateSqlSpec;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.relation.guild.GuildRelation;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class InsertGuildRelationSpec extends UpdateSqlSpec<Void> {

    private final int guildId1, guildId2;

    private final GuildRelation relation;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "INSERT INTO `%s` (`guild_id_min`, `guild_id_max`, `type`) VALUES(?, ?, ?);",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_RELATIONS_TABLE_NAME
            );
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, Math.min(guildId1, guildId2));
            statement.setInt(2, Math.max(guildId1, guildId2));
            statement.setString(3, relation.name());

            return statement;
        };
    }

}
