package br.com.legion.guilds.storage.specs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import br.com.idea.api.shared.storage.repositories.specs.InsertSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.guilds.Guild;
import br.com.legion.guilds.GuildsConstants;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InsertGuildSpec extends InsertSqlSpec<Guild> {

    private final String tag;

    private final String name;

    private final int maxMembers;

    private final double gloryPoints;

    @Override
    public Guild parser(int affectedRows, ResultSet keyHolder) throws SQLException {
        if (affectedRows != 1) {
            return null;
        }

        return new Guild(
                keyHolder.getInt("id"),
                tag,
                name,
                maxMembers,
                new Date(System.currentTimeMillis()),
                gloryPoints
        );
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "INSERT INTO `%s` (`tag`, `name`, `max_members`, `gloryPoints`) VALUES(?, ?, ?, ?);",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, tag);
            statement.setString(2, name);
            statement.setInt(3, maxMembers);
            statement.setDouble(4, gloryPoints);

            return statement;
        };
    }
}
