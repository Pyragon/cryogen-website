package com.cryo.cache;

import lombok.Data;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 24, 2017 at 5:01:45 AM
 */
@Data
public abstract class CachedItem {
	
	protected final String cacheName;
	
	protected long cacheTime;
	
	protected Object cachedData;
	
	public CachedItem(String cacheName) {
		this.cacheName = cacheName;
	}
	
	public abstract void fetchNewData(Object... values);
	
	public abstract long getCacheTimeLimit();
	
	public Object getCachedData(Object... values) {
		if (hasExpired(values)) {
			refreshTime();
			fetchNewData(values);
		}
		return cachedData;
	}

	protected void clear() {
		this.cacheTime = 0;
	}

	protected boolean hasExpired(Object... values) {
		return this.cacheTime <= System.currentTimeMillis();
	}
	
	protected void refreshTime() {
		this.cacheTime = System.currentTimeMillis() + getCacheTimeLimit();
	}
	
}
