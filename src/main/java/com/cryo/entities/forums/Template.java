package com.cryo.entities.forums;

import com.cryo.entities.accounts.Account;
import lombok.Data;

import static com.cryo.Website.getConnection;

@Data
public class Template {

    private final int id;
    private final String type;
    private final String body;

    public String getName(Account account) {
        if(type.equals("forum")) {
            SubForum forum = getConnection("cryogen_forum").selectClass("subforums", "id=?", SubForum.class, id);
            if(forum == null) return "Invalid Forum";
            if(!forum.getPermissions().canSeeForum(account)) return "Invalid Forum";
            return forum.getName();
        } else if(type.equals("thread")) {
            Thread thread = getConnection("cryogen_forum").selectClass("threads", "id=?", Thread.class, id);
            if(thread == null) return "Invalid Thread";
            if(!thread.getSubForum().getPermissions().canSeeForum(account) || !thread.getSubForum().getPermissions().canReply(thread, account)) return "Invalid Thread";
            return thread.getTitle();
        }
        return "Invalid Type";
    }

    public String getTitle(Account account) {
        if(hasPermissions(account)) return "";
        return "You do not have permission to post in this "+type+". Are you logged in?";
    }

    public boolean hasPermissions(Account account) {
        if(account == null) return false;
        if(type.equals("forum")) {
            SubForum forum = getConnection("cryogen_forum").selectClass("subforums", "id=?", SubForum.class, id);
            if(forum == null) return false;
            if(!forum.getPermissions().canSeeForum(account)) return false;
            return true;
        } else if(type.equals("thread")) {
            Thread thread = getConnection("cryogen_forum").selectClass("threads", "id=?", Thread.class, id);
            if(thread == null) return false;
            if(!thread.getSubForum().getPermissions().canSeeForum(account) || !thread.getSubForum().getPermissions().canReply(thread, account)) return false;
            return true;
        }
        return false;
    }

}
