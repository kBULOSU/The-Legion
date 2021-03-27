package br.com.legion.glory.points.storage;

import br.com.idea.api.shared.providers.MysqlDatabaseProvider;
import br.com.idea.api.shared.storage.repositories.MysqlRepository;
import br.com.legion.glory.points.storage.specs.*;

import java.util.Map;

public class GloryPointsRepository extends MysqlRepository {

    public GloryPointsRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);

        query(new CreateGPTableSpec());
    }

    public double fetch(String name) {
        return query(new SelectGPSpec(name));
    }

    public void insertOrUpdate(String name, double points) {
        query(new InsertOrUpdateGPSpec(name, points));
    }

    public boolean delete(String name) {
        return query(new DeleteGPSpec(name));
    }

    public Map<String, Double> fetchTop(int index) {
        return query(new SelectGPTopSpec(index));
    }
}
