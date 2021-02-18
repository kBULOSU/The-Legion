package br.com.legion.guilds.relation.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GuildRole {

    MEMBER("Membro", "Membros", "+"),
    CAPTAIN("Capitão", "Capitães", "*"),
    LEADER("Líder", "Líderes", "#");

    private final static GuildRole[] VALS = values();

    @Getter
    private final String displayName;

    @Getter
    private final String displayPluralName;

    @Getter
    private final String symbol;

    public GuildRole next() {
        return VALS[(this.ordinal() + 1) % VALS.length];
    }

    public GuildRole previous() {
        int index = (this.ordinal() - 1) % VALS.length;
        return VALS[index >= 0 ? index : VALS.length - 1];
    }

    public boolean isHigher(GuildRole role) {
        return this.ordinal() > role.ordinal();
    }

    public boolean isSameOrHigher(GuildRole role) {
        return this.ordinal() >= role.ordinal();
    }
}
