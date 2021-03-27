package br.com.legion.glory.points.transactions.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.ResultSetExtractor;
import br.com.idea.api.shared.storage.repositories.specs.SelectSqlSpec;
import br.com.legion.glory.points.transactions.Transaction;
import br.com.legion.glory.points.transactions.TransactionType;
import com.google.common.base.Enums;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
public class SelectTransactionsByIdSpec extends SelectSqlSpec<List<Transaction>> {

    private final Integer id;

    @Override
    public ResultSetExtractor<List<Transaction>> getResultSetExtractor() {
        return resultSet -> {
            List<Transaction> out = Lists.newArrayList();

            while (resultSet.next()) {
                out.add(
                        new Transaction(
                                resultSet.getInt("sourceId"),
                                Enums.getIfPresent(TransactionType.class, resultSet.getString("type")).orNull(),
                                resultSet.getTimestamp("transactedAt"),
                                resultSet.getDouble("amount")
                        )
                );
            }

            return out;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `id` = ?;",
                    "gp_transactions"
            );

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            return preparedStatement;
        };
    }
}
