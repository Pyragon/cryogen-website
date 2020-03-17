package com.cryo.entities.forums;

import com.cryo.Website;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        return GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, authorId);
    }

    public Thread getThread() {
        return ForumConnection.connection().selectClass("threads", "id=?", Thread.class, threadId);
    }

    public ArrayList<Account> getThanks() {
        ArrayList<Account> accounts = new ArrayList<>();
        Object data = Website.instance().getCachingManager().getData("thanks-cache", "thanks", id);
        if (data == null) return accounts;
        return (ArrayList<Account>) data;
    }

    public boolean hasBeenThankedBy(int id) {
        return getThanks().stream()
                        .filter(a -> a.getId() == id)
                        .findFirst()
                        .isPresent();
    }

    public boolean hasBeenEdited() {
        return edited != null;
    }

    public String getFormattedPost() {
        return Website.instance().getBBCodeManager().getFormattedPost(post);
    }

    public void editPost(String post) {
        this.post = post;
        ForumConnection.connection().set("posts", "post=?", "id=?", post, id);
    }

    public String getTimeRelative(Timestamp stamp) {
        if (stamp == null) stamp = added;
        Date now = new Date();
        long diff = DateUtils.getDateDiff(now, stamp, TimeUnit.DAYS);
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
