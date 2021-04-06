package br.com.legion.economy.storage;

import br.com.idea.api.shared.providers.MysqlDatabaseProvider;
import br.com.idea.api.shared.storage.repositories.MysqlRepository;
import br.com.legion.economy.storage.specs.*;

import java.util.Map;

public class EconomyRepository extends MysqlRepository {

    public EconomyRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);

        query(new CreateEconomyTableSpec());
    }

    public double fetch(String name) {
        return query(new SelectEconomyUserSpec(name));
    }

    public void insertOrUpdate(String name, double points) {
        query(new InsertOrUpdateEconomyUserSpec(name, points));
    }

    public boolean delete(String name) {
        return query(new DeleteEconomySpec(name));
    }

    public Map<String, Double> fetchTop(int index) {
        return query(new SelectEconomyTopSpec(index));
    }
}
