package br.com.legion.guilds.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.UpdateSqlSpec;
import br.com.legion.guilds.GuildsConstants;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class UpdateGuildLastMaintenanceSpec extends UpdateSqlSpec<Void> {

    private final int id;
    private final long lastMaintenance;

    @Override
    public Void parser(int i) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "UPDATE `%s` SET `lastMaintenance` = ? WHERE `id` = ? LIMIT 1;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_TABLE_NAME
            );

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setTimestamp(1, new Timestamp(lastMaintenance));
            preparedStatement.setInt(2, id);

            return preparedStatement;
        };
    }
}
