package com.cryo.cache.impl;

import java.util.Properties;

import com.cryo.cache.CachedItem;
import com.cryo.managers.api.ServerConnection;
import com.google.gson.Gson;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 31, 2017 at 2:40:34 AM
 */
public class DropsFeedCache extends CachedItem {
	
	public DropsFeedCache() {
		super("drops-feed-cache");
	}

	@Override
	public void fetchNewData(Object... values) {
		String url = ServerConnection.SERVER_URL+"/grab_data?action=get-drops-feed";
		String response = "";//ServerConnection.getResponse(url);
		if(response.equals("")) return;
		this.cachedData = new Gson().fromJson(response, Properties.class);
	}
	
	@Override
	public long getCacheTimeLimit() {
		return 5000;
	}
	
}
