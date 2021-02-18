package br.com.legion.guilds.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.UpdateSqlSpec;
import br.com.legion.guilds.GuildsConstants;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
public class DeleteGuildSpec extends UpdateSqlSpec<Boolean> {

    private final int factionId;

    @Override
    public Boolean parser(int affectedRows) {
        return affectedRows == 1;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "DELETE FROM `%s` WHERE `id` = ? LIMIT 1;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, factionId);

            return statement;
        };
    }
}
