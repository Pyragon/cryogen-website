package com.cryo.entities;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Notification extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int userId;
    private final String faIcon;
    private final String icon;
    private final String title;
    private final String content;
    private final String link;
    private final boolean read;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

}
