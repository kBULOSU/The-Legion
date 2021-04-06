package br.com.legion.economy.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.DeleteSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.economy.EconomyConstants;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class DeleteEconomySpec extends DeleteSqlSpec<Boolean> {

    private final String name;

    @Override
    public Boolean parser(int i) {
        return i == 1;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "DELETE FROM `%s` WHERE `name` = ? LIMIT 1",
                    EconomyConstants.Tables.ECONOMY_TABLE
            );

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);

            return preparedStatement;
        };
    }
}
