package br.com.legion.guilds;

import br.com.idea.api.shared.messages.MessageUtils;
import lombok.*;

import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class Guild {

    private final Integer id;

    @Setter
    @NonNull
    private String tag;

    @Setter
    @NonNull
    private String name;

    @Setter
    private int level;

    @Setter
    private int maxMembers;

    @Setter
    private double gloryPoints;

    @Setter
    private double bankLimit;

    private final Date createdAt;

    public Guild(Integer id, String tag, String name, int level, int maxMembers, Date createdAt) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.level = level;
        this.maxMembers = maxMembers;
        this.createdAt = createdAt;
        this.gloryPoints = 0.0;
        this.bankLimit = GuildsConstants.Config.getBankLimitByLevel(level);
    }

    public Guild(Integer id, String tag, String name, int level, int maxMembers, double gloryPoints, Date createdAt) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.level = level;
        this.maxMembers = maxMembers;
        this.gloryPoints = gloryPoints;
        this.bankLimit = GuildsConstants.Config.getBankLimitByLevel(level);
        this.createdAt = createdAt;
    }

    public String getDisplayName() {
        return MessageUtils.translateColorCodes(String.format(
                "[%s] %s", tag.toUpperCase(), name
        ));
    }
}
