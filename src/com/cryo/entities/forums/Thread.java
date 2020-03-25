package com.cryo.entities.forums;

import com.cryo.Website;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.modules.account.entities.Account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Thread extends MySQLDao {

    @MySQLDefault
    @MySQLRead
    private int id;
    private final int forumId;
    private final String title;
    private final int authorId;
    @MySQLRead
    private int firstPostId;
    @MySQLRead
    private int lastPostId;
    @MySQLRead
    private int lastPostAuthor;
    @MySQLRead
    private Timestamp lastPostTime;
    private final boolean hasPoll;
    private final int pollId;
    @MySQLRead
    private int views;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public SubForum getSubForum() {
        return ForumConnection.connection().selectClass("subforums", "id=?", SubForum.class, forumId);
    }

    public Post getLastPost() {
        return ForumConnection.connection().selectClass("posts", "id=?", Post.class, lastPostId);
    }

    public long getLastPostLong() {
        return lastPostTime == null ? 0L : lastPostTime.getTime();
    }

    public Account getAuthor() {
        return GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, authorId);
    }

    public void addView() {
        views++;
        ForumConnection.connection().set("threads", "views=?", "id=?", views, id);
    }

    public void updateLastPost(Post post) {
        lastPostId = post.getId();
        lastPostAuthor = post.getAuthorId();
        lastPostTime = post.getAdded();
        ForumConnection.connection().set("threads", "last_post_id=?, last_post_author=?, last_post_time=?", "id=?", lastPostId, lastPostAuthor, lastPostTime, id);
    }

    public void updateFirstPost(Post post) {
        firstPostId = post.getId();
        ForumConnection.connection().set("threads", "first_post_id=?", "id=?", firstPostId, id);
    }

    public int getPostCount() {
        return ForumConnection.connection().selectCount("posts", "thread_id=?", id);
    }

    public List<Account> getViewers() {
        return ForumConnection.connection()
                            .selectList("account_statuses", "thread_id=? && expiry > CURRENT_TIMESTAMP()", AccountStatus.class, id)
                            .stream().map(as -> as.getAccount())
                            .collect(Collectors.toList());
    }

    public ArrayList<Post> getPosts(int page) {
        if(page == 0) page = 1;
        int offset = (page - 1) * 10;
        return ForumConnection.connection().selectList("posts", "thread_id=?", "LIMIT ?,10", Post.class, id, offset);
    }

}
