package br.com.legion.glory.points.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.InsertSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.glory.points.GloryPointsConstants;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class InsertOrUpdateGPSpec extends InsertSqlSpec<Void> {

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
                    "INSERT INTO `%s` (`name`, `points`) VALUES (?,?) ON DUPLICATE KEY UPDATE `points`=VALUES(points);",
                    GloryPointsConstants.Tables.GLORY_POINTS_TABLE
            );

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, points);

            return preparedStatement;
        };
    }
}
