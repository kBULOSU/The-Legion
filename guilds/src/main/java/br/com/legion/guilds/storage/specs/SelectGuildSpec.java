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
                int level = result.getInt("level");
                Date createdAt = result.getTimestamp("created_at");
                int maxMembers = result.getInt("max_members");
                double gloryPoints = result.getDouble("gloryPoints");
                long lastMaintenance = result.getTimestamp("lastMaintenance").getTime();

                Guild guild = new Guild(id, tag, name, level, maxMembers, gloryPoints, createdAt, lastMaintenance);

                return guild;
            }

            return null;
        };
    }
}
