package com.cryo.cache;

import java.io.IOException;

import com.cryo.Website;
import com.cryo.cache.store.Store;
import com.cryo.entities.annotations.WebStart;
import com.cryo.entities.annotations.WebStartSubscriber;
import com.cryo.utils.Logger;

@WebStartSubscriber
public final class Cache {

	public static Store STORE;

	private Cache() {

	}
	
	public static void init(String path) throws IOException {
		STORE = new Store(path);
	}

	public static void init() throws IOException {
		STORE = new Store(Website.getProperties().getProperty("cache_path"));
	}

	@WebStart(priority = 0)
	public static void loadCache() {
		long start = System.currentTimeMillis();

		try {
			init();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		long total = System.currentTimeMillis()-start;
		Logger.log(Cache.class, "Loaded cache in "+(total)+"ms");
	}
}
