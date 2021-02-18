package br.com.legion.guilds.relation.user.storage.specs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import br.com.idea.api.shared.storage.repositories.specs.InsertSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InsertOrUpdateGuildUserRelationSpec extends InsertSqlSpec<Boolean> {

    private final GuildUserRelation relation;

    @Override
    public Boolean parser(int affectedRows, ResultSet keyHolder) {
        return affectedRows > 0;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(String.format(
                    "INSERT INTO `%s` (`user_id`, `guild_id`, `rank`, `since`) VALUE (?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "`guild_id`= VALUES(`guild_id`), "
                    + "`rank`= VALUES(`rank`), "
                    + "`since`= VALUES(`since`);",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_USERS_RELATIONS_TABLE_NAME
            ), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, relation.getUserId());
            statement.setInt(2, relation.getGuildId());
            statement.setString(3, relation.getRole().name());
            statement.setTimestamp(4, new Timestamp(relation.getSince().getTime()));

            return statement;
        };
    }
}
