package com.cryo.cache.impl.forums;

import com.cryo.cache.CachedItem;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.UserGroup;

import java.util.HashMap;

public class UsergroupCache extends CachedItem {

    private HashMap<Integer, UserGroup> usergroup;
    private HashMap<Integer, Long> usergroupTimes;

    public UsergroupCache() {
        super("usergroup-cache");
        usergroup = new HashMap<>();
        usergroupTimes = new HashMap<>();
    }

    @Override
    public Object getCachedData(Object... values) {
        int id = (int) values[0];
        if(hasExpired(values)) fetchNewData(values);
        if(!usergroup.containsKey(id)) return null;
        return usergroup.get(id);
    }

    @Override
    public void fetchNewData(Object... values) {
        int id = (int) values[0];
        UserGroup group = ForumConnection.connection().selectClass("usergroups", "id=?", UserGroup.class, id);
        if(group == null)
            return;
        usergroup.put(id, group);
        usergroupTimes.put(id, System.currentTimeMillis()+getCacheTimeLimit());
    }

    @Override
    public boolean hasExpired(Object... values) {
        int id = (int) values[0];
        if(!usergroupTimes.containsKey(id)) return true;
        return usergroupTimes.get(id) < System.currentTimeMillis();
    }

    @Override
    public void clear() {
        usergroupTimes.clear();
    }

    @Override
    public long getCacheTimeLimit() {
        return 120_000;
    }
}
