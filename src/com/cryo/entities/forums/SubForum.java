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
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class SubForum extends MySQLDao {

    @MySQLDefault
    private final int id;
    @MySQLRead
    private String name;
    @MySQLRead
    private String description;
    @MySQLRead
    private int parentId;
    @MySQLRead
    private boolean isCategory;
    @MySQLRead
    private int permissionsId;
    @MySQLRead
    private int priority;
    @MySQLRead
    private String link;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public ArrayList<SubForum> getSubForums() {
        return ForumConnection.connection().selectList("subforums", "parent_id=?", "ORDER BY priority ASC", SubForum.class, id);
    }

    public boolean isLink() {
        return link != null && !link.equals("");
    }

    public Object[] createBreadcrumbs(ArrayList<String> crumbs, ArrayList<String> links) {
        SubForum forum = this;
        if(crumbs == null || links == null) {
            crumbs = new ArrayList<>();
            links = new ArrayList<>();
        }
        while(forum.getParentId() != -1) {
            crumbs.add(forum.getName());
            links.add("/forums/forum/" + forum.getId());
            forum = forum.getParent();
        }
        crumbs.add(forum.getName());
        links.add("/forums");
        crumbs.add("Home");
        links.add("/forums");
        return new Object[] { crumbs, links };
    }

    public SubForum getParent() {
        return ForumConnection.connection().selectClass("subforums", "id=?", SubForum.class, parentId);
    }

    public int getTotalViews() {
        return getThreads().stream().mapToInt(Thread::getViews).sum();
    }

    public int getTotalThreads() {
        return getThreads().size();
    }

    public int getTotalPosts() {
        return getThreads().stream().mapToInt(Thread::getPostCount).sum();
    }

    public Post getLastPost() {
        if(isLink()) return null;
        long latest = 0L;
        Post post = null;
        if(!isCategory) {
            if(getThreads().size() > 0) {
                Optional<Thread> optional = getThreads().stream().sorted(
                        Comparator.comparingLong(Thread::getLastPostLong)
                                .reversed())
                        .findAny();
                if(optional.isPresent()) {
                    Thread nThread = optional.get();
                    if(nThread.getLastPost() != null) {
                        post = optional.get().getLastPost();
                        latest = post.getAdded().getTime();
                    }
                }
            }
        }
        for(SubForum forum : getSubForums()) {
            if(forum.isLink()) continue;
            Post nPost = forum.getLastPost();
            if(nPost != null) {
                if(post == null || nPost.getAdded().getTime() > latest)
                    post = nPost;
            }
        }
        return post;
    }

    public ArrayList<Thread> getThreads() {
        ArrayList<Thread> results = new ArrayList<>();
        ArrayList<Thread> list = ForumConnection.connection().selectList("threads", "forum_id=? && archived=0", Thread.class, id);
        results.addAll(list.stream().filter(t -> t.isPinned()).sorted(Comparator.comparing(Thread::getAdded)).collect(Collectors.toList()));
        results.addAll(list.stream().filter(t -> !t.isPinned()).sorted(Comparator.comparing(Thread::getLastPostTime).reversed()).collect(Collectors.toList()));
        return results;
    }

    public Permissions getPermissions() {
        if (permissionsId == -1) {
            SubForum parent = getParent();
            if (parent == null)
                return ForumConnection.connection().selectClass("permissions", "id=?", Permissions.class, 2);
            return parent.getPermissions();
        }
        return ForumConnection.connection().selectClass("permissions", "id=?", Permissions.class, permissionsId);
    }

}
