package com.cryo.entities.forums;

import java.sql.Timestamp;

import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.modules.account.entities.Account;

import lombok.Data;

@Data
public class AccountStatus extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int accountId;
    private final int index;
    private final int forumId;
    private final int userId;
    private final int threadId;
    private final Timestamp expiry;
    
    public Account getAccount() {
        return GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, accountId);
    }

    public Thread getThread() {
        if(threadId == -1) return null;
        return ForumConnection.connection().selectClass("threads", "id=?", Thread.class, threadId);
    }

    public Account getViewing() {
        if(userId == -1) return null;
        return GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, userId);
    }

    public SubForum getForum() {
        if(forumId == -1) return null;
        return ForumConnection.connection().selectClass("subforums", "id=?", SubForum.class, forumId);
    }

}