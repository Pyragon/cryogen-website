package com.cryo.entities.forums;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.AccountStatus;
import com.cryo.modules.accounts.AccountUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cryo.Website.getConnection;

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
    @MySQLDefault
    private boolean open;
    @MySQLRead
    private boolean pinned;
    @MySQLRead
    private int views;
    @MySQLDefault
    @MySQLRead
    private boolean archived;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public SubForum getSubForum() {
        return getConnection("cryogen_forum").selectClass("subforums", "id=?", SubForum.class, forumId);
    }

    public Post getFirstPost() {
        return getConnection("cryogen_forum").selectClass("posts", "id=?", Post.class, firstPostId);
    }

    public Post getLastPost() {
        return getConnection("cryogen_forum").selectClass("posts", "id=?", Post.class, lastPostId);
    }

    public long getLastPostLong() {
        return lastPostTime == null ? 0L : lastPostTime.getTime();
    }

    public Account getAuthor() {
        return AccountUtils.getAccount(authorId);
    }

    public void addView() {
        views++;
        getConnection("cryogen_forum").set("threads", "views=?", "id=?", views, id);
    }

    public void setStatus(boolean open) {
        this.open = open;
        getConnection("cryogen_forum").set("threads", "open=?", "id=?", open, id);
    }

    public void updateLastPost(Post post) {
        lastPostId = post.getId();
        lastPostAuthor = post.getAuthorId();
        lastPostTime = post.getAdded();
        getConnection("cryogen_forum").set("threads", "last_post_id=?, last_post_author=?, last_post_time=?", "id=?", lastPostId, lastPostAuthor, lastPostTime, id);
    }

    public void updateFirstPost(Post post) {
        firstPostId = post.getId();
        getConnection("cryogen_forum").set("threads", "first_post_id=?", "id=?", firstPostId, id);
    }

    public void updatePinned(boolean pinned) {
        this.pinned = pinned;
        getConnection("cryogen_forum").set("threads", "pinned=?", "id=?", pinned, id);
    }

    public void archive() {
        this.archived = true;
        getConnection("cryogen_forum").set("threads", "archived=?", "id=?", archived, id);
    }

    public int getPostCount() {
        return getConnection("cryogen_forum").selectCount("posts", "thread_id=?", id);
    }

    public List<Account> getViewers() {
        return getConnection("cryogen_forum")
                            .selectList("account_statuses", "thread_id=? && expiry > CURRENT_TIMESTAMP()", AccountStatus.class, id)
                            .stream().map(as -> as.getAccount())
                            .collect(Collectors.toList());
    }

    public ArrayList<Post> getPosts(int page) {
        if(page == 0) page = 1;
        int offset = (page - 1) * 10;
        return getConnection("cryogen_forum").selectList("posts", "thread_id=?", "LIMIT ?,10", Post.class, id, offset);
    }

}
