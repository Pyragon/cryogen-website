package com.cryo.entities.forums;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.entities.accounts.Account;
import com.cryo.managers.BBCodeManager;
import com.cryo.modules.accounts.AccountUtils;
import com.cryo.utils.FormatUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.cryo.Website.getConnection;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Post extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int threadId;
    private final int authorId;
    @MySQLRead
    private String post;
    @MySQLDefault
    private final Timestamp added;
    private final Timestamp edited;
    @MySQLDefault
    private final Timestamp updated;

    public Account getAuthor() {
        return AccountUtils.getAccount(authorId);
    }

    public Thread getThread() {
        return getConnection("cryogen_forum").selectClass("threads", "id=?", Thread.class, threadId);
    }

    public boolean hasBeenEdited() {
        return edited != null;
    }

    public String getFormattedPost(Account account) {
        return BBCodeManager.getFormattedPost(account, this);
    }

    public ArrayList<String> getCSS() {
        return BBCodeManager.getCSS(post);
    }

    public void editPost(String post) {
        this.post = post;
        getConnection("cryogen_forum").set("posts", "post=?", "id=?", post, id);
    }

    public String getTimeRelative(Timestamp stamp) {
        if (stamp == null) stamp = added;
        Date now = new Date();
        long diff = FormatUtils.getDateDiff(now, stamp, TimeUnit.DAYS);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String timeF = format.format(stamp);
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
        return format.format(stamp) + " @ " + timeF;
    }

}
