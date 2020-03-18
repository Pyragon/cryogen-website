package com.cryo.entities.forums;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.modules.account.entities.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class Permissions extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int type;
    @MySQLRead("can_see")
    private final String canSeeString;
    @MySQLRead("can_read_threads")
    private final String canReadThreadsString;
    @MySQLRead("can_reply")
    private final String canReplyString;
    @MySQLRead("can_edit")
    private final String canEditString;
    @MySQLRead("can_create_thread")
    private final String canCreateThreadString;
    @MySQLRead("can_create_polls")
    private final String canCreatePollsString;
    private ArrayList<Integer> canSeeList;
    private ArrayList<Integer> canReadThreadsList;
    private ArrayList<Integer> canReplyList;
    private ArrayList<Integer> canEditList;
    private ArrayList<Integer> canCreateThreadList;
    private ArrayList<Integer> canCreatePollsList;

    //-1 = everyone
    //-2 = everyone but guests
    //-3 = only your own thread
    //-4 = only your own thread + staff threads

    public boolean canSeeForum(Account account) {
        loadCanSee();
        return matching(canSeeList, account);
    }

    public boolean canReadThread(Thread thread, Account account) {
        loadCanReadThreads();
        if(matching(canReadThreadsList, account)) return true;
        if(thread.getAuthorId() == account.getId() && canReadThreadsList.contains(-3)) return true;
        if(thread.getAuthor().getRights() > 0 && canReadThreadsList.contains(-4)) return true;
        return false;
    }

    public boolean canReply(Thread thread, Account account) {
        loadCanReply();
        if(matching(canReplyList, account)) return true;
        if (thread.getAuthorId() == account.getId() && canReplyList.contains(-3))
            return true;
        return false;
    }

    public boolean canEdit(Post post, Account account) {
        loadCanEdit();
        if (matching(canEditList, account))
            return true;
        if (post.getAuthorId() == account.getId() && canEditList.contains(-3))
            return true;
        return false;
    }
    
    public boolean canCreateThread(Account account) {
        loadCanCreateThreads();
        return matching(canCreateThreadList, account);
    }

    public boolean matching(ArrayList<Integer> permissions, Account account) {
        if (permissions.contains(-1))
            return true;
        if (account != null && permissions.contains(-2))
            return true;
        if(account == null) return false;
        if(permissions.contains(account.getDisplayGroup().getId())) return true;
        ArrayList<Integer> copy = new ArrayList<>(permissions);
        List<Integer> groupsI = account.getUsergroups().stream().map(UserGroup::getId).collect(Collectors.toList());
        copy.retainAll(groupsI);
        return copy.size() > 0;
    }

    public void loadCanCreateThreads() {
        if (canCreateThreadList == null) {
            ArrayList<Double> dbls = Website.getGson().fromJson(canCreateThreadString, ArrayList.class);
            canCreateThreadList = dbls.stream().mapToInt(d -> d.intValue()).boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public void loadCanSee() {
        if (canSeeList == null) {
            ArrayList<Double> dbls = Website.getGson().fromJson(canSeeString, ArrayList.class);
            canSeeList = dbls.stream().mapToInt(d -> d.intValue()).boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public void loadCanReadThreads() {
        if (canReadThreadsList == null) {
            ArrayList<Double> dbls = Website.getGson().fromJson(canReadThreadsString, ArrayList.class);
            canReadThreadsList = dbls.stream().mapToInt(d -> d.intValue()).boxed().collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public void loadCanReply() {
        if (canReplyList == null) {
            ArrayList<Double> dbls = Website.getGson().fromJson(canReplyString, ArrayList.class);
            canReplyList = dbls.stream().mapToInt(d -> d.intValue()).boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public void loadCanEdit() {
        if (canEditList == null) {
            ArrayList<Double> dbls = Website.getGson().fromJson(canEditString, ArrayList.class);
            canEditList = dbls.stream().mapToInt(d -> d.intValue()).boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

}
