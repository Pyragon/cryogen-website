package com.cryo.entities.accounts.support;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReportVerification extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int verifyId;
    private final String username;
    private final Timestamp expiry;
    @MySQLDefault
    private final Timestamp added;
}
