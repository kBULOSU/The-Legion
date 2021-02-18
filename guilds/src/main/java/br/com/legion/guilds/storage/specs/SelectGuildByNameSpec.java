package br.com.legion.guilds.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.guilds.GuildsConstants;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class SelectGuildByNameSpec extends SelectGuildSpec {

    private final String name;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            PreparedStatement statement = connection.prepareStatement(String.format(
                    "SELECT * FROM `%s` WHERE `name` = ?;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_TABLE_NAME
            ));

            statement.setString(1, this.name);

            return statement;
        };
    }
}
