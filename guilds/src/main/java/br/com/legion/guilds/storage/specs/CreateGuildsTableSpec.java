package br.com.legion.guilds.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.ExecuteSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCallback;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.guilds.GuildsConstants;

public class CreateGuildsTableSpec extends ExecuteSqlSpec<Void> {

    @Override
    public PreparedStatementCallback<Void> getPreparedStatementCallback() {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "CREATE TABLE IF NOT EXISTS `%s` (`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                            "`tag` CHAR(3) NOT NULL, `name` VARCHAR(255) NOT NULL, " +
                            "`max_members` INT NOT NULL, `gloryPoints` DOUBLE NOT NULL);",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_TABLE_NAME
            );

            return connection.prepareStatement(query);
        };
    }
}
