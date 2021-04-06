package br.com.legion.economy.transactions.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.InsertSqlSpec;
import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.legion.economy.transactions.Transaction;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@RequiredArgsConstructor
public class InsertTransactionSpec extends InsertSqlSpec<Void> {

    private final Integer id;
    private final Transaction transaction;

    @Override
    public Void parser(int i, ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return connection -> {
            String query = String.format(
                    "INSERT INTO `%s` (`id`, `sourceId`, `type`, `transactedAt`, `amount`) VALUES (?,?,?,?,?);",
                    "economy_transactions"
            );

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, transaction.getSourceId() == null ? 0 : transaction.getSourceId());
            preparedStatement.setString(3, transaction.getType().name());
            preparedStatement.setTimestamp(4, new Timestamp(transaction.getTransactedAt().getTime()));
            preparedStatement.setDouble(5, transaction.getAmount());

            return preparedStatement;
        };
    }
}
