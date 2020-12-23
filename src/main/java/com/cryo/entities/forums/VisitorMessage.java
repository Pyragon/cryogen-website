package com.cryo.entities.forums;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.accounts.Account;
import com.cryo.modules.accounts.AccountUtils;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class VisitorMessage extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int accountId;
    private final int authorId;
    private final String message;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public Account getAuthor() {
        return AccountUtils.getAccount(authorId);
    }
}
