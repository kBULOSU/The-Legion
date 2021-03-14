package br.com.legion.guilds;

import br.com.idea.api.shared.messages.MessageUtils;
import br.com.legion.guilds.maintenance.GuildMaintenanceRunnable;
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
    private long lastMaintenance;

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

    public void doMaintenance() {
        if (needMaintenance()) {
            new GuildMaintenanceRunnable(this).run();
        }
    }

    public void upgradeLevel() {
        this.maxMembers = GuildsConstants.Config.getMaxMembersByLevel(this.level);
        this.bankLimit = GuildsConstants.Config.getBankLimitByLevel(this.level);
        this.level++;
    }

    public boolean needMaintenance() {
        long lastMaintenanceDif = System.currentTimeMillis() - this.getLastMaintenance();
        long lastMaintenanceDifSeconds = lastMaintenanceDif / 1000;
        long lastMaintenanceDifMinutes = lastMaintenanceDifSeconds / 60;
        long lastMaintenanceDifHours = lastMaintenanceDifMinutes / 60;

        return lastMaintenanceDifHours >= GuildsConstants.Config.MAINTENANCE_COOLDOWN;
    }
}
