package com.cryo.cache.impl.forums;

import com.cryo.cache.CachedItem;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.SubForum;

import java.util.ArrayList;
import java.util.HashMap;

public class SubforumListCache extends CachedItem {

    public SubforumListCache() {
        super("subforum-list-cache");
        categoryRefreshTimes = new HashMap<>();
        forumRefreshTimes = new HashMap<>();
        categorySubForums = new HashMap<>();
        forumSubForums = new HashMap<>();
    }

    private HashMap<Integer, Long> categoryRefreshTimes;
    private HashMap<Integer, Long> forumRefreshTimes;

    private HashMap<Integer, ArrayList<SubForum>> categorySubForums;
    private HashMap<Integer, ArrayList<SubForum>> forumSubForums;

    @Override
    public Object getCachedData(Object... values) {
        boolean isCategory = (boolean) values[0];
        int id = (int) values[1];
        if(hasExpired(values))
            fetchNewData(values);
        HashMap<Integer,  ArrayList<SubForum>> toCheck = isCategory ? categorySubForums : forumSubForums;
        if(!toCheck.containsKey(id)) return null;
        return toCheck.get(id);
    }

    @Override
    public void fetchNewData(Object... values) {
        boolean isCategory = (boolean) values[0];
        int id = (int) values[1];
        ArrayList<SubForum> list = ForumConnection.connection().selectList("subforums", "parent_id=? AND parent_is_category=?", SubForum.class, id, isCategory);
        if(list == null) return;
        if(isCategory) categorySubForums.put(id, list);
        else forumSubForums.put(id, list);
        HashMap<Integer, Long> toAdd = isCategory ? categoryRefreshTimes : forumRefreshTimes;
        toAdd.put(id, System.currentTimeMillis()+getCacheTimeLimit());
    }

    @Override
    protected boolean hasExpired(Object... values) {
        boolean isCategory = (boolean) values[0];
        int id = (int) values[1];
        HashMap<Integer, Long> toCheck = isCategory ? categoryRefreshTimes : forumRefreshTimes;
        if(!toCheck.containsKey(id)) return true;
        long lastChecked = toCheck.get(id);
        return lastChecked <= System.currentTimeMillis();
    }

    @Override
    public void clear() {
        categoryRefreshTimes.clear();
        forumRefreshTimes.clear();
    }

    @Override
    public long getCacheTimeLimit() {
        return 120_000;
    }
}
