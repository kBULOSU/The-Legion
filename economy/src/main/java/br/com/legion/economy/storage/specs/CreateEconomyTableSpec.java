package br.com.legion.economy.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.ExecuteSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCallback;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.economy.EconomyConstants;

public class CreateEconomyTableSpec extends ExecuteSqlSpec<Void> {

    @Override
    public PreparedStatementCallback<Void> getPreparedStatementCallback() {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = String.format(
                    "CREATE TABLE IF NOT EXISTS `%s` (`name` VARCHAR(16) PRIMARY KEY NOT NULL, `balance` DOUBLE NOT NULL DEFAULT 0);",
                    EconomyConstants.Tables.ECONOMY_TABLE
            );

            return connection.prepareStatement(query);
        };
    }
}
