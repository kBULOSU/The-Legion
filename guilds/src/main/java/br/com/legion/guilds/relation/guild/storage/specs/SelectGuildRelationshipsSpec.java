package br.com.legion.guilds.relation.guild.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.ResultSetExtractor;
import br.com.idea.api.shared.storage.repositories.specs.SelectSqlSpec;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.relation.guild.GuildRelation;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;

@RequiredArgsConstructor
public class SelectGuildRelationshipsSpec extends SelectSqlSpec<Set<Integer>> {

    private final int guildId1;

    private final GuildRelation relation;

    @Override
    public ResultSetExtractor<Set<Integer>> getResultSetExtractor() {
        return (ResultSet result) -> {
            Set<Integer> output = Sets.newHashSet();

            while (result.next()) {
                int id1 = result.getInt("guild_id_min");
                int id2 = result.getInt("guild_id_max");

                output.add(id1 == guildId1 ? id2 : id1);
            }

            return output;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE (`guild_id_min`=? OR `guild_id_max`=?) AND `type`=?;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_RELATIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, guildId1);
            statement.setInt(2, guildId1);
            statement.setString(3, relation.name());
            return statement;
        };
    }
}
