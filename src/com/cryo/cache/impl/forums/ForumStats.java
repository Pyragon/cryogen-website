package com.cryo.cache.impl.forums;

import com.cryo.cache.CachedItem;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.Post;
import com.cryo.modules.account.entities.Account;
import com.cryo.entities.forums.Thread;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ForumStats extends CachedItem {

    public ForumStats() {
        super("forum-stats");
    }

    @Override
    public void fetchNewData(Object... values) {
        Account account = (Account) values[0];
        int totalThreads = ForumConnection.connection().selectCount("threads", "archived=0");
        int totalPosts = ForumConnection.connection().selectCount("posts", null);
        List<Post> latestPosts = ForumConnection.connection()
                .selectList("posts", null, "LIMIT 20", Post.class, null)
                .stream()
                .filter(p -> !p.getThread().isArchived())
                .filter(p -> p.getThread().getSubForum().getPermissions().canReadThread(p.getThread(), account))
                .sorted(Comparator.comparing(Post::getAdded)
                        .reversed()).limit(5)
                .collect(Collectors.toList());
        List<Thread> latestThreads = ForumConnection.connection()
                .selectList("threads", "archived=0", "LIMIT 20", Thread.class)
                .stream()
                .filter(t -> t.getSubForum().getPermissions().canReadThread(t, account))
                .sorted(Comparator.comparing(Thread::getAdded).reversed()).limit(5).collect(Collectors.toList());
        this.cachedData = new Object[] { totalThreads, totalPosts, latestPosts, latestThreads };
    }

    @Override
    public long getCacheTimeLimit() {
        return 30_000;
    }
}
