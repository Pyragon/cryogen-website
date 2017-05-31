package com.cryo.cache.impl;

import java.util.HashMap;

import com.cryo.cache.CachedItem;
import com.cryo.server.ServerConnection;
import com.cryo.server.ServerItem;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 30, 2017 at 8:47:05 PM
 */
public class ServerItemCache extends CachedItem {
	
	private HashMap<String, Object[]> cache;

	public ServerItemCache() {
		super("server-item-cache");
		cache = new HashMap<>();
	}
	
	@Override
	public Object getCachedData(Object... data) {
		String name = (String) data[0];
		boolean expired = hasExpired(name);
		if(expired) fetchNewData(data);
		if(!cache.containsKey(name)) return null;
		return (ServerItem) cache.get(name)[1];
	}
	
	public boolean hasExpired(String name) {
		if(!cache.containsKey(name)) return true;
		Object[] data = cache.get(name);
		return (long) data[0] < System.currentTimeMillis();
	}

	@Override
	public void fetchNewData(Object... data) {
		String name = (String) data[0];
		ServerItem item = ServerConnection.getServerItem(name);
		if(item == null) return;
		cache.put(name, new Object[] { System.currentTimeMillis() + getCacheTimeLimit(), item });
	}

	@Override
	public long getCacheTimeLimit() {
		return 5 * 60 * 1000; //5 minutes
	}
	
}
