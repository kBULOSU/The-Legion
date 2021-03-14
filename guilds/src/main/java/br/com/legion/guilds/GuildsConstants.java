package br.com.legion.guilds;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Pattern;

public class GuildsConstants {

    public static final int TAG_MAX_LENGTH = 3;
    public static final int TAG_MIN_LENGTH = 2;

    public static final int NAME_MAX_LENGTH = 20;
    public static final int NAME_MIN_LENGTH = 5;

    public static final int GUILD_DEFAULT_MAX_MEMBERS = 30;

    public static final Pattern TAG_PATTERN = Pattern.compile(String.format("[a-zA-Z0-9]{%s,%s}", TAG_MIN_LENGTH, TAG_MAX_LENGTH));

    // [A-Za-zÀ-ÖØ-öø-ÿ ]{5,20}
    public static final Pattern NAME_PATTERN = Pattern.compile(String.format("[a-zA-Z0-9 ]{%s,%s}", NAME_MIN_LENGTH, NAME_MAX_LENGTH));

    public static final String MONEY_HEAD_KEY = "7cc4cf8a56a01fa4794184aa11d1b603b76df16a8282dfc10e9c46060d32768a";

    public static class Databases {

        public static class Mysql {

            public static class Tables {

                public static final String GUILDS_TABLE_NAME = "guilds";

                public static final String GUILDS_USERS_RELATIONS_TABLE_NAME = "users_guilds";

                public static final String GUILDS_RELATIONS_TABLE_NAME = "guilds_relations";

            }
        }
    }

    public static class Config {

        private static final FileConfiguration CONFIG = GuildsPlugin.getInstance().getConfig();

        public static final double PRICE_TO_CREATE = CONFIG.getDouble("price-to-create");

        public static final int HOURS_TO_CREATE = CONFIG.getInt("hours-to-create");

        public static final int BANK_LIMIT_I = CONFIG.getInt("bank-limit.1");
        public static final int BANK_LIMIT_II = CONFIG.getInt("bank-limit.2");
        public static final int BANK_LIMIT_III = CONFIG.getInt("bank-limit.3");

        public static final int MAINTENANCE_I = CONFIG.getInt("maintenance.1");
        public static final int MAINTENANCE_II = CONFIG.getInt("maintenance.2");
        public static final int MAINTENANCE_III = CONFIG.getInt("maintenance.3");
        public static final int MAINTENANCE_COOLDOWN = CONFIG.getInt("maintenance.cooldown");

        public static final double LEVEL_UPGRADE_PRICE_II = CONFIG.getDouble("price-to-upgrade.1-2");
        public static final double LEVEL_UPGRADE_PRICE_III = CONFIG.getDouble("price-to-upgrade.2-3");

        public static final int MAX_MEMBERS_I = CONFIG.getInt("max-members.1");
        public static final int MAX_MEMBERS_II = CONFIG.getInt("max-members.2");
        public static final int MAX_MEMBERS_III = CONFIG.getInt("max-members.3");

        public static int getBankLimitByLevel(int level) {
            switch (level) {
                case 1:
                    return BANK_LIMIT_I;
                case 2:
                    return BANK_LIMIT_II;
                case 3:
                    return BANK_LIMIT_III;
                default:
                    return 0;
            }
        }

        public static int getMaintenanceTaxByLevel(int level) {
            switch (level) {
                case 1:
                    return MAINTENANCE_I;
                case 2:
                    return MAINTENANCE_II;
                case 3:
                    return MAINTENANCE_III;
                default:
                    return 0;
            }
        }

        public static int getMaxMembersByLevel(int level) {
            switch (level) {
                case 1:
                    return MAX_MEMBERS_I;
                case 2:
                    return MAX_MEMBERS_II;
                case 3:
                    return MAX_MEMBERS_III;
                default:
                    return 5;
            }
        }
    }
}
