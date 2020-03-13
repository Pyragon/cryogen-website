package com.cryo.entities;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Misc extends MySQLDao {

    private final String name;
    @MySQLRead
    private String value;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp edited;

    public Misc(String name, long value) {
        this(name, Long.toString(value), null, null);
    }

    public Misc(String name, int value) {
        this(name, Integer.toString(value), null, null);
    }

    public Misc(String name, String value, Timestamp added, Timestamp edited) {
        this.name = name;
        this.value = value;
        this.added = added;
        this.edited = edited;
    }

    public int asInt() {
        return Integer.parseInt(value);
    }

    public long asLong() {
        return Long.parseLong(value);
    }

}