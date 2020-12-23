package com.cryo.entities.accounts;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CurrentDisplayName extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String username;
    private final String displayName;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

}
