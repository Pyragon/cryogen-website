package com.cryo.cache.impl.forums;

import com.cryo.cache.CachedItem;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.Permissions;

import java.util.HashMap;

public class PermissionsCache extends CachedItem {

    private HashMap<Integer, Permissions> permissions;
    private HashMap<Integer, Long> permissionsTimes;

    public PermissionsCache() {
        super("permissions-cache");
        permissions = new HashMap<>();
        permissionsTimes = new HashMap<>();
    }

    @Override
    public Object getCachedData(Object... values) {
        int id = (int) values[0];
        if(hasExpired(values)) fetchNewData(values);
        if(!permissions.containsKey(id)) return null;
        return permissions.get(id);
    }

    @Override
    public void fetchNewData(Object... values) {
        int id = (int) values[0];
        Permissions permissions = ForumConnection.connection().selectClass("permissions", "id=?", Permissions.class, id);
        if(permissions == null)
            return;
        this.permissions.put(id, permissions);
        permissionsTimes.put(id, System.currentTimeMillis()+getCacheTimeLimit());
    }

    @Override
    public boolean hasExpired(Object... values) {
        int id = (int) values[0];
        if(!permissionsTimes.containsKey(id)) return true;
        return permissionsTimes.get(id) < System.currentTimeMillis();
    }

    @Override
    public void clear() {
        permissionsTimes.clear();
    }

    @Override
    public long getCacheTimeLimit() {
        return 120_000;
    }
}
