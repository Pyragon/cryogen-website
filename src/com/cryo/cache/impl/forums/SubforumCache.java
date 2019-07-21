package com.cryo.cache.impl.forums;

import com.cryo.cache.CachedItem;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.SubForum;

import java.util.HashMap;

public class SubforumCache extends CachedItem {

    private HashMap<Integer, SubForum> subforums;
    private HashMap<Integer, Long> subforumsTimes;

    public SubforumCache() {
        super("subforums-cache");
        subforums = new HashMap<>();
        subforumsTimes = new HashMap<>();
    }

    @Override
    public Object getCachedData(Object... values) {
        int id = (int) values[0];
        if(hasExpired(values)) fetchNewData(values);
        if(!subforums.containsKey(id)) return null;
        return subforums.get(id);
    }

    @Override
    public void fetchNewData(Object... values) {
        int id = (int) values[0];
        SubForum forum = ForumConnection.connection().selectClass("subforums", "id=?", SubForum.class, id);
        if(forum == null)
            return;
        subforums.put(id, forum);
        subforumsTimes.put(id, System.currentTimeMillis()+getCacheTimeLimit());
    }

    @Override
    public boolean hasExpired(Object... values) {
        int id = (int) values[0];
        if(!subforumsTimes.containsKey(id)) return true;
        return subforumsTimes.get(id) < System.currentTimeMillis();
    }

    @Override
    public long getCacheTimeLimit() {
        return 120_000;
    }
}
