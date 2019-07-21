package com.cryo.cache.impl.forums;

import com.cryo.cache.CachedItem;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.Category;

public class CategoryListCache extends CachedItem {

    public CategoryListCache() {
        super("category-list-cache");
    }

    @Override
    public void fetchNewData(Object... values) {
        this.cachedData = ForumConnection.connection().selectList("categories", "", "ORDER BY priority ASC", Category.class);
    }

    @Override
    public long getCacheTimeLimit() {
        return 120_000;
    }
}
