package br.com.legion.guilds.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.ResultSetExtractor;
import br.com.idea.api.shared.storage.repositories.specs.SelectSqlSpec;
import br.com.legion.guilds.Guild;

import java.sql.ResultSet;
import java.util.Date;

public abstract class SelectGuildSpec extends SelectSqlSpec<Guild> {

    @Override
    public ResultSetExtractor<Guild> getResultSetExtractor() {
        return (ResultSet result) -> {
            if (result.next()) {
                int id = result.getInt("id");
                String tag = result.getString("tag").toUpperCase();
                String name = result.getString("name");
                Date createdAt = result.getTimestamp("created_at");
                int maxMembers = result.getInt("max_members");
                double gloryPoints = result.getDouble("gloryPoints");

                Guild guild = new Guild(id, tag, name, maxMembers, createdAt, gloryPoints);

                return guild;
            }

            return null;
        };
    }
}
