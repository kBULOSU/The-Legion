package br.com.legion.guilds.relation.guild.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.UpdateSqlSpec;
import br.com.legion.guilds.GuildsConstants;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class DeleteGuildRelationSpec extends UpdateSqlSpec<Void> {

    private final int factionId1, factionId2;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "DELETE FROM `%s` WHERE `guild_id_min` = ? AND `guild_id_max` = ? LIMIT 1;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_RELATIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, Math.min(factionId1, factionId2));
            statement.setInt(2, Math.max(factionId1, factionId2));

            return statement;
        };
    }

}
