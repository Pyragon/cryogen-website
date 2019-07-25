package com.cryo.entities.forums;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.modules.account.entities.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class Permissions extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int type;
    @MySQLRead("can_read_threads")
    private final String canReadThreadsString;
    @MySQLRead("can_read_own_threads")
    private final String canReadOwnThreadsString;
    @MySQLRead("can_create_thread")
    private final String canCreateThreadString;
    @MySQLRead("can_read_staff_threads")
    private final String canReadStaffThreadsString;
    @MySQLRead("can_create_polls")
    private final String canCreatePollsString;
    private ArrayList<Integer> canReadThreadsList;
    private ArrayList<Integer> canReadOwnThreadsList;
    private ArrayList<Integer> canCreateThreadList;
    private ArrayList<Integer> canReadStaffThreadsList;
    private ArrayList<Integer> canCreatePollsList;

    public boolean canReadThread(Account account) {
        loadCanReadThreads();
        System.out.println(canReadThreadsList + " " + account.getDisplayGroup().getId());
        if (account == null) return canReadThreadsList.contains(-1);
        if (canReadThreadsList.contains(account.getDisplayGroup().getId()))
            return true;
        if (account.getUsergroups() != null) {
            for (UserGroup group : account.getUsergroups())
                if (canReadThreadsList.contains(group.getId()))
                    return true;
        }
        return false;
    }

    public void loadCanReadThreads() {
        if (canReadThreadsList == null) {
            ArrayList<Double> dbls = Website.getGson().fromJson(canReadThreadsString, ArrayList.class);
            canReadThreadsList = dbls.stream().mapToInt(d -> d.intValue()).boxed().collect(Collectors.toCollection(ArrayList::new));
        }
    }

}
