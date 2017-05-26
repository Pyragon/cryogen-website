package com.cryo.cache;

import java.io.IOException;
import java.util.HashMap;

import com.cryo.utils.Utilities;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 24, 2017 at 5:00:48 AM
 */
public class CachingManager {
	
	private HashMap<String, CachedItem> cachedItems;
	
	public CachingManager() {
		cachedItems = new HashMap<>();
	}
	
	public void loadCachedItems() {
		try {
			for(Class<?> c : Utilities.getClasses("com.cryo.cache.impl")) {
				if(c.isAnonymousClass()) continue;
				Object obj = c.newInstance();
				if(obj == null || !(obj instanceof CachedItem)) continue;
				CachedItem item = (CachedItem) obj;
				cachedItems.put(item.cacheName, item);
			}
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Unable to load cache data.", e);
		}
	}
	
	public CachedItem get(String opcode) {
		if(!cachedItems.containsKey(opcode)) return null;
		return cachedItems.get(opcode);
	}
	
}
