package br.com.legion.guilds;

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

        public static final double PRICE_TO_CREATE = GuildsPlugin.getInstance().getConfig().getInt("price-to-create");

        public static final int HOURS_TO_CREATE = GuildsPlugin.getInstance().getConfig().getInt("hours-to-create");

        public static final int BANK_LIMIT_I = GuildsPlugin.getInstance().getConfig().getInt("bank-limit.1");
        public static final int BANK_LIMIT_II = GuildsPlugin.getInstance().getConfig().getInt("bank-limit.2");
        public static final int BANK_LIMIT_III = GuildsPlugin.getInstance().getConfig().getInt("bank-limit.3");

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
    }
}
