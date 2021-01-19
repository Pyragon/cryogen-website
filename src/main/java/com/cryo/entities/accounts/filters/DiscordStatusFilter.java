package com.cryo.entities.accounts.filters;

public enum DiscordStatusFilter implements Filter {

    SENT("Sent", "discord_status", 3),
    NOT_SENT("Not Sent", "discord_status", 3, true);

    private final String name;
    private final String key;
    private final Object value;
    private final boolean not;

    DiscordStatusFilter(String name, String key, Object value) {
        this(name, key, value, false);
    }

    DiscordStatusFilter(String name, String key, Object value, boolean not) {
        this.name = name;
        this.key = key;
        this.value = value;
        this.not = not;
    }

    public String getName() {
        return name();
    }

    public String title() {
        return name;
    }

    public String key() {
        return key;
    }

    public Object value() {
        return value;
    }

    public boolean not() { return not; }
}
