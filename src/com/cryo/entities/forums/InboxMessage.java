package com.cryo.entities.forums;

import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.modules.account.entities.Account;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class InboxMessage extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int accountId;
    private final int fromId;
    private final String subject;
    private final String body;
    private final boolean read;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public Account getAuthor() {
        return GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, fromId);
    }

}
