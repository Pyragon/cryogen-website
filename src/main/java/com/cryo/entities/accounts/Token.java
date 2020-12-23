package com.cryo.entities.accounts;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Data
public class Token extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String username;
    private final String token;
    @MySQLDefault
    private final Timestamp expiry;
    @MySQLDefault
    private final Timestamp added;

}
