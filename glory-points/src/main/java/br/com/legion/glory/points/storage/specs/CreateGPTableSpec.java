package br.com.legion.glory.points.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.ExecuteSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCallback;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.glory.points.GloryPointsConstants;

public class CreateGPTableSpec extends ExecuteSqlSpec<Void> {

    @Override
    public PreparedStatementCallback<Void> getPreparedStatementCallback() {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {

            String query = String.format(
                    "CREATE TABLE IF NOT EXISTS `%s` (`name` VARCHAR(16) PRIMARY KEY NOT NULL, `points` DOUBLE NOT NULL);",
                    GloryPointsConstants.Tables.GLORY_POINTS_TABLE
            );

            return connection.prepareStatement(query);
        };
    }
}
