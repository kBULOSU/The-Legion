package br.com.legion.guilds.relation.user;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "userId")
public class GuildUserRelation {

    private final int userId;

    //

    private int guildId;

    @Setter
    private GuildRole role;

    private Date since;
}
