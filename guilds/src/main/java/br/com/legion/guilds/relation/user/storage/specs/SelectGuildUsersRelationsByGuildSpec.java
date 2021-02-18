package br.com.legion.guilds.relation.user.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.ResultSetExtractor;
import br.com.idea.api.shared.storage.repositories.specs.SelectSqlSpec;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import com.google.common.base.Enums;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
public class SelectGuildUsersRelationsByGuildSpec extends SelectSqlSpec<Set<GuildUserRelation>> {

    private final int guildId;

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {

        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `guild_id` = ?;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_USERS_RELATIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, this.guildId);

            return statement;
        };

    }

    @Override
    public ResultSetExtractor<Set<GuildUserRelation>> getResultSetExtractor() {
        return (ResultSet result) -> {
            Set<GuildUserRelation> out = Sets.newHashSet();

            while (result.next()) {
                int userId = result.getInt("user_id");
                GuildRole rank = Enums.getIfPresent(GuildRole.class, result.getString("rank")).orNull();
                Date since = result.getTimestamp("since");

                out.add(new GuildUserRelation(userId, guildId, rank, since));
            }

            return out;
        };
    }
}
