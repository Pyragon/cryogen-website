package com.cryo.entities;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MiscData extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String name;
    private final String value;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

}
