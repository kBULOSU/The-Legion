package br.com.legion.guilds.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.guilds.GuildsConstants;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class SelectGuildByIdSpec extends SelectGuildSpec {

    private final Integer id;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `id` = ?;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_TABLE_NAME
            ));

            statement.setInt(1, this.id);

            return statement;
        };

    }
}
