package com.cryo.entities.accounts.discord;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Verify extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final long discordId;
    private final String random;
    @MySQLDefault
    private final Timestamp added;

}
