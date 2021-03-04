package br.com.legion.guilds.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.UpdateSqlSpec;
import br.com.legion.guilds.GuildsConstants;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
public class UpdateGuildBankByIdSpec extends UpdateSqlSpec<Void> {

    private final Integer id;
    private final double gloryPoints;

    @Override
    public Void parser(int i) {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "UPDATE `%s` SET `gloryPoints` = ? WHERE `id` = ? LIMIT 1;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_TABLE_NAME
            );

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, gloryPoints);
            preparedStatement.setInt(2, id);

            return preparedStatement;
        };
    }
}
