package com.cryo.entities.forums;

import com.cryo.Website;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

@Data
public class SubForum extends ForumParent {

    @MySQLDefault
    private final int id;
    private final String name;
    private final String description;
    private final int parentId;
    private final boolean parentIsCategory;
    private final boolean isCategory;
    private final boolean isLink;
    private final int permissionsId;
    private final int priority;
    private final String link;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public SubForum(int id, String name, String description, int parentId, boolean parentIsCategory, boolean isCategory, boolean isLink, 
        int permissions, int priority, String link, Timestamp updated, Timestamp added) {
            super(permissions);
            this.id = id;
            this.name = name;
            this.description = description;
            this.parentId = parentId;
            this.parentIsCategory = parentIsCategory;
            this.isCategory = isCategory;
            this.isLink = isLink;
            this.permissionsId = permissions;
            this.priority = priority;
            this.link = link;
            this.added = added;
            this.updated = updated;
            setParent(getParent());
        }

    public ArrayList<SubForum> getSubForums() {
        Object data = Website.instance().getCachingManager().getData("subforum-list-cache", false, id);
        if(!(data instanceof ArrayList)) return null;
        return (ArrayList<SubForum>) data;
    }

    public Object[] createBreadcrumbs(ArrayList<String> crumbs, ArrayList<String> links) {
        Object obj = this;
        if(crumbs == null || links == null) {
            crumbs = new ArrayList<>();
            links = new ArrayList<>();
        }
        while(!(obj instanceof Category)) {
            SubForum forum = (SubForum) obj;
            crumbs.add(forum.getName());
            links.add("/forums/forum/"+forum.getId());
            obj = forum.getParent();
        }
        Category category = (Category) obj;
        crumbs.add(category.getName());
        links.add("/forums");
        crumbs.add("Home");
        links.add("/forums");
        return new Object[] { crumbs, links };
    }

    public ForumParent getParent() {
        if(parentIsCategory) return ForumConnection.connection().selectClass("categories", "id=?", Category.class, parentId);
        else return ForumConnection.connection().selectClass("subforums", "id=?", SubForum.class, parentId);
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
        return ForumConnection.connection().selectList("threads", "forum_id=?", Thread.class, id);
    }

}
