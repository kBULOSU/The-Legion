package br.com.legion.guilds.relation.guild.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.ExecuteSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCallback;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.guilds.GuildsConstants;

public class CreateGuildRelationTableSpec extends ExecuteSqlSpec<Void> {

    @Override
    public PreparedStatementCallback<Void> getPreparedStatementCallback() {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "CREATE TABLE IF NOT EXISTS `%s` (`guild_id_min` INT NOT NULL, `guild_id_max` INT NOT NULL, `type` VARCHAR(255) NOT NULL);",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_RELATIONS_TABLE_NAME
            );

            return connection.prepareStatement(query);
        };
    }
}
