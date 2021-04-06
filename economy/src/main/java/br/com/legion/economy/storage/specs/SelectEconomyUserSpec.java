package br.com.legion.economy.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.ResultSetExtractor;
import br.com.idea.api.shared.storage.repositories.specs.SelectSqlSpec;
import br.com.legion.economy.EconomyConstants;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class SelectEconomyUserSpec extends SelectSqlSpec<Double> {

    private final String name;

    @Override
    public ResultSetExtractor<Double> getResultSetExtractor() {
        return resultSet -> resultSet.next() ? resultSet.getDouble("balance") : null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "SELECT `points` FROM `%s` WHERE `name` = ? LIMIT 1;",
                    EconomyConstants.Tables.ECONOMY_TABLE
            );

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);

            return preparedStatement;
        };
    }
}
