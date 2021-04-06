package br.com.legion.economy.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.ResultSetExtractor;
import br.com.idea.api.shared.storage.repositories.specs.SelectSqlSpec;
import br.com.legion.economy.EconomyConstants;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class SelectEconomyTopSpec extends SelectSqlSpec<Map<String, Double>> {

    private final int index;

    @Override
    public ResultSetExtractor<Map<String, Double>> getResultSetExtractor() {
        return resultSet -> {
            Map<String, Double> out = Maps.newLinkedHashMap();

            while (resultSet.next()) {
                out.put(
                        resultSet.getString("name"),
                        resultSet.getDouble("balance")
                );
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "SELECT * FROM `%s` ORDER BY `balance` DESC LIMIT %s;",
                    EconomyConstants.Tables.ECONOMY_TABLE,
                    index
            );

            return connection.prepareStatement(query);
        };
    }
}
