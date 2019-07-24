package com.cryo.entities.forums;

import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
public class Post extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int threadId;
    private final int authorId;
    private final String post;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public Account getAuthor() {
        return GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, authorId);
    }

    public Thread getThread() {
        return ForumConnection.connection().selectClass("threads", "id=?", Thread.class, threadId);
    }

    public String getTimeRelative() {
        Date now = new Date();
        long diff = DateUtils.getDateDiff(now, added, TimeUnit.DAYS);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String timeF = format.format(added);
        format = new SimpleDateFormat("MM/dd/yyyy");
        diff = Math.abs(diff);
        String result = "";
        if (diff == 0)
            result = "Today @ ";
        else if (diff == 1)
            result = "Yesterday @ ";
        else if (diff <= 7)
            result = diff + " days ago @ ";
        if (diff <= 7)
            return result + timeF;
        return format.format(added) + " @ " + timeF;
    }

}
