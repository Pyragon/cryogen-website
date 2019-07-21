package com.cryo.entities.forums;

import com.cryo.Website;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class Thread extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int forumId;
    private final String title;
    private final int author;
    @MySQLRead
    private int lastPostId;
    @MySQLRead
    private int lastPostAuthor;
    @MySQLRead
    private Timestamp lastPostTime;
    private final boolean hasPoll;
    private final int pollId;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public SubForum getSubForum() {
        Object data = Website.instance().getCachingManager().getData("subforums-cache", forumId);
        if(data == null) return null;
        return (SubForum) data;
    }

    public Post getLastPost() {
        return ForumConnection.connection().selectClass("posts", "id=?", Post.class, lastPostId);
    }

    public long getLastPostLong() {
        return lastPostTime == null ? 0L : lastPostTime.getTime();
    }

    public void updateLastPost(Post post) {
        lastPostId = post.getId();
        lastPostAuthor = post.getAuthor();
        lastPostTime = post.getAdded();
        ForumConnection.connection().set("threads", "last_post_id=?, last_post_author=?, last_post_time=?", "id=?", post.getId(), post.getAuthor(), post.getAdded(), id);
    }

    public ArrayList<Post> getPosts(int page) {
        if(page == 0) page = 1;
        int offset = (page - 1) * 10;
        return ForumConnection.connection().selectList("posts", "thread_id=?", "LIMIT ?,10", Post.class, id, offset);
    }

}
