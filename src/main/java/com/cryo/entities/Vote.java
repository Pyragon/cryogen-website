package com.cryo.entities;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Vote extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String username;
    private final int siteId;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

}
