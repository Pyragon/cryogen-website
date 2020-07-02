package com.cryo.entities.forums;

import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.modules.account.entities.Account;
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
        return GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, authorId);
    }
}
