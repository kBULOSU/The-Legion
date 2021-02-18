package br.com.legion.guilds.relation.user.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.ExecuteSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCallback;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.guilds.GuildsConstants;

public class CreateGuildUserRelationTableSpec extends ExecuteSqlSpec<Void> {

    @Override
    public PreparedStatementCallback<Void> getPreparedStatementCallback() {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "CREATE TABLE IF NOT EXISTS `%s` (`user_id` INT NOT NULL PRIMARY KEY,"
                            + " `guild_id` INT NOT NULL, `rank` VARCHAR(255) NOT NULL,"
                            + " `since` TIMESTAMP NOT NULL);",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_USERS_RELATIONS_TABLE_NAME
            );

            return connection.prepareStatement(query);
        };
    }

}
