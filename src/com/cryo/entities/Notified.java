package com.cryo.entities;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Notified extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int userId;
    private final int postId;
    @MySQLDefault
    private final Timestamp added;
}
