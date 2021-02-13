package br.com.legion.glory.points.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.ResultSetExtractor;
import br.com.idea.api.shared.storage.repositories.specs.SelectSqlSpec;
import br.com.legion.glory.points.GloryPointsConstants;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class SelectGPTopSpec extends SelectSqlSpec<Map<String, Double>> {

    private final int index;

    @Override
    public ResultSetExtractor<Map<String, Double>> getResultSetExtractor() {
        return resultSet -> {
            Map<String, Double> out = Maps.newLinkedHashMap();

            while (resultSet.next()) {
                out.put(
                        resultSet.getString("name"),
                        resultSet.getDouble("points")
                );
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "SELECT * FROM `%s` ORDER BY `points` DESC LIMIT %s;",
                    GloryPointsConstants.Tables.GLORY_POINTS_TABLE,
                    index
            );

            return connection.prepareStatement(query);
        };
    }
}
