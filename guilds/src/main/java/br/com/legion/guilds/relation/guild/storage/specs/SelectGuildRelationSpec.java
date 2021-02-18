package br.com.legion.guilds.relation.guild.storage.specs;

import br.com.idea.api.shared.storage.repositories.specs.PreparedStatementCreator;
import br.com.idea.api.shared.storage.repositories.specs.ResultSetExtractor;
import br.com.idea.api.shared.storage.repositories.specs.SelectSqlSpec;
import br.com.legion.guilds.GuildsConstants;
import br.com.legion.guilds.relation.guild.GuildRelation;
import com.google.common.base.Enums;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@RequiredArgsConstructor
public class SelectGuildRelationSpec extends SelectSqlSpec<GuildRelation> {

    private final int guildId1, guildId2;

    @Override
    public ResultSetExtractor<GuildRelation> getResultSetExtractor() {
        return (ResultSet result) -> {
            if (result.next()) {
                return Enums.getIfPresent(GuildRelation.class, result.getString("type"))
                        .or(GuildRelation.NONE);
            }

            return GuildRelation.NONE;
        };
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        return (Connection con) -> {
            String query = String.format(
                    "SELECT * FROM `%s` WHERE `guild_id_min`=? AND `guild_id_max`=?;",
                    GuildsConstants.Databases.Mysql.Tables.GUILDS_RELATIONS_TABLE_NAME
            );

            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, Math.min(guildId1, guildId2));
            statement.setInt(2, Math.max(guildId1, guildId2));
            return statement;
        };
    }
}
