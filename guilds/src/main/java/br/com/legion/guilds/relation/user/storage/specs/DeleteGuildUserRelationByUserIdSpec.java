package br.com.legion.guilds.relation.user.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.DeleteSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.guilds.GuildsConstants;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class DeleteGuildUserRelationByUserIdSpec extends DeleteSqlSpec<Void> {

    private final Integer userId;

    @Override
    public Void parser(int affectedRows) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "DELETE FROM %s WHERE `user_id` = ?;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_USERS_RELATIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, userId);
            return statement;
        };
    }

}
