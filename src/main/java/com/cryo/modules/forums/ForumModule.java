package com.cryo.modules.forums;

import com.cryo.entities.accounts.Account;
import com.cryo.entities.forums.UserGroup;

public class ForumModule {

    public static String crownUser(Account account, int height, int width) {
        StringBuilder builder = new StringBuilder();
        if(account.getImageBefore() != null && !account.getImageBefore().equals(""))
            builder.append("<img class='forum-name' src='"+account.getImageBefore()+"' style='height: "+height+"px; width: "+width+"px; vertical-align: middle;'/>");
        builder.append("<span style="+(account.getNameColour() == null ? "''" : "'color: "+account.getNameColour()+";'")+">"+account.getDisplayName()+"</span>");
        if (account.getImageAfter() != null && !account.getImageAfter().equals(""))
            builder.append("<img class='forum-name' src='" + account.getImageAfter() + "' style='height: " + height + "px; width: " + width + "px; vertical-align: middle;'/>");
        return builder.toString();
    }

    public static String crownUser(UserGroup group, int height, int width) {
        StringBuilder builder = new StringBuilder();
        if(group.getImageBefore() != null && !group.getImageBefore().equals(""))
            builder.append("<img class='forum-name' src='"+group.getImageBefore()+"' style='height: "+height+"px; width: "+width+"px; vertical-align: middle;'/>");
        builder.append("<span style="+(group.getColour() == null ? "''" : "'color: "+group.getColour()+";'")+">"+group.getName()+"</span>");
        if (group.getImageAfter() != null && !group.getImageAfter().equals(""))
            builder.append("<img class='forum-name' src='" + group.getImageAfter() + "' style='height: " + height + "px; width: " + width + "px; vertical-align: middle;'/>");
        return builder.toString();
    }
}
