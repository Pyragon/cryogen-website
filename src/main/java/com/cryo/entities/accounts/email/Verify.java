package com.cryo.entities.accounts.email;

import com.cryo.entities.MySQLDao;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Verify extends MySQLDao {

    private final int id;
    private final String username;
    private final String random;
    private final String email;
    private final Timestamp expiry;
}
