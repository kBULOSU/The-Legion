package br.com.legion.economy.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.InsertSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.economy.EconomyConstants;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class InsertOrUpdateEconomyUserSpec extends InsertSqlSpec<Void> {

    private final String name;
    private final double points;

    @Override
    public Void parser(int i, ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = String.format(
                    "INSERT INTO `%s` (`name`, `balance`) VALUES (?,?) ON DUPLICATE KEY UPDATE `balance`=VALUES(balance);",
                    EconomyConstants.Tables.ECONOMY_TABLE
            );

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, points);

            return preparedStatement;
        };
    }
}
