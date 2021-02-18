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
    private int maxMembers;

    @Setter
    private double gloryPoints;

    private final Date createdAt;

    public Guild(Integer id, String tag, String name, int maxMembers, Date createdAt) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.maxMembers = maxMembers;
        this.createdAt = createdAt;
        this.gloryPoints = 0.0;
    }

    public Guild(Integer id, String tag, String name, int maxMembers, Date createdAt, double bank) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.maxMembers = maxMembers;
        this.createdAt = createdAt;
        this.gloryPoints = bank;
    }

    public String getDisplayName() {
        return MessageUtils.translateColorCodes(String.format(
                "[%s] %s", tag.toUpperCase(), name
        ));
    }
}
