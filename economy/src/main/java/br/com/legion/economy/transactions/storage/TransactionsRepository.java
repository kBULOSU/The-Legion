package br.com.legion.economy.transactions.storage;

import br.com.idea.api.shared.providers.MysqlDatabaseProvider;
import br.com.idea.api.shared.storage.repositories.MysqlRepository;
import br.com.legion.economy.transactions.Transaction;
import br.com.legion.economy.transactions.storage.specs.CreateTransactionsTableSpec;
import br.com.legion.economy.transactions.storage.specs.InsertTransactionSpec;
import br.com.legion.economy.transactions.storage.specs.SelectTransactionsByIdSpec;

import java.util.List;

public class TransactionsRepository extends MysqlRepository {

    public TransactionsRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);

        query(new CreateTransactionsTableSpec());
    }

    public List<Transaction> fetch(Integer id) {
        return query(new SelectTransactionsByIdSpec(id));
    }


    public void insert(Integer id, Transaction transaction) {
        query(new InsertTransactionSpec(id, transaction));
    }
}
