package com.cryo.entities.accounts.email;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Email extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String username;
    @MySQLRead
    private String email;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

}