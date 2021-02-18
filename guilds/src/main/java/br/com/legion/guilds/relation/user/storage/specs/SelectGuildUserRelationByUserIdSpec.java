package br.com.legion.guilds.relation.user.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.ResultSetExtractor;
import br.com.idea.api.shared.storage.repositories.specs.SelectSqlSpec;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.relation.user.GuildRole;
import br.com.legion.guilds.relation.user.GuildUserRelation;
import com.google.common.base.Enums;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

@RequiredArgsConstructor
public class SelectGuildUserRelationByUserIdSpec extends SelectSqlSpec<GuildUserRelation> {

    private final Integer userId;

    @Override
    public ResultSetExtractor<GuildUserRelation> getResultSetExtractor() {
        return (ResultSet result) -> {
            if (result.next()) {
                int guildId = result.getInt("guild_id");
                GuildRole rank = Enums.getIfPresent(GuildRole.class, result.getString("rank")).orNull();
                Date since = result.getTimestamp("since");

                return new GuildUserRelation(userId, guildId, rank, since);
            }

            return null;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `user_id` = ?;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_USERS_RELATIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query);

            statement.setInt(1, this.userId);

            return statement;
        };
    }
}
