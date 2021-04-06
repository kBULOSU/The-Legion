package br.com.legion.economy.transactions.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.ExecuteSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCallback;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;

public class CreateTransactionsTableSpec extends ExecuteSqlSpec<Void> {

    @Override
    public PreparedStatementCallback<Void> getPreparedStatementCallback() {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "CREATE TABLE IF NOT EXISTS `%s` (`id` INT NOT NULL, `sourceId` INT NOT NULL, `type` VARCHAR(255) NOT NULL, " +
                            "`transactedAt` TIMESTAMP NOT NULL, `amount` DOUBLE NOT NULL);",
                    "economy_transactions"
            );

            return connection.prepareStatement(query);
        };
    }
}
