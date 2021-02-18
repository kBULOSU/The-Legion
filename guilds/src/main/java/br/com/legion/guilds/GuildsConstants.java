package br.com.legion.guilds;

import java.util.regex.Pattern;

public class GuildsConstants {

    public static final int TAG_MAX_LENGTH = 3;
    public static final int TAG_MIN_LENGTH = 2;

    public static final int NAME_MAX_LENGTH = 20;
    public static final int NAME_MIN_LENGTH = 5;

    public static final int GUILD_DEFAULT_MAX_MEMBERS = 30;

    public static final double PRICE_TO_CREATE = 100000.0;

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
}
