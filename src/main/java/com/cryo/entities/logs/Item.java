package com.cryo.entities.logs;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Item extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int itemId;
    private final int amount;
    private final String uid;
    @MySQLDefault
    private final Timestamp added;
}
