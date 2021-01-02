package com.cryo.entities.accounts;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.forums.SubForum;
import com.cryo.entities.forums.Thread;
import com.cryo.modules.account.AccountUtils;
import lombok.Data;

import java.sql.Timestamp;

import static com.cryo.Website.getConnection;

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
        return AccountUtils.getAccount(accountId);
    }

    public Thread getThread() {
        if(threadId == -1) return null;
        return getConnection("cryogen_forum").selectClass("threads", "id=?", Thread.class, threadId);
    }

    public Account getViewing() {
        if(userId == -1) return null;
        return AccountUtils.getAccount(userId);
    }

    public SubForum getForum() {
        if(forumId == -1) return null;
        return getConnection("cryogen_forum").selectClass("subforums", "id=?", SubForum.class, forumId);
    }

}