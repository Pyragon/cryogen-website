package com.cryo.cache.impl;

import java.util.HashMap;

import com.cryo.cache.CachedItem;
import com.cryo.modules.highscores.HSDataList;
import com.cryo.modules.highscores.HSUserCache;
import com.cryo.modules.highscores.HSUtils;
import com.cryo.modules.highscores.HSUtils.HSData;
import com.cryo.utils.Logger;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 24, 2017 at 5:03:42 AM
 */
public class HighscoresCache extends CachedItem {
	
	private HashMap<String, HSUserCache> userData;
	
	private HSDataList mini_list;
	private long mini_list_expiry;
	
	private HSDataList global_list;
	private long global_list_expiry;

	public HighscoresCache() {
		super("hs-cache");
		userData = new HashMap<>();
	}

	@Override
	public void fetchNewData(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "personal":
				String username = (String) data[1];
				HSData hs_data = HSUtils.getHSData(username);
				long expiry = System.currentTimeMillis() + getCacheTimeLimit();
				userData.put(username, new HSUserCache(hs_data, expiry));
				break;
			case "mini-list":
				mini_list = HSUtils.getList(10);
				mini_list_expiry = System.currentTimeMillis() + getCacheTimeLimit();
				break;
			case "global-list":
				global_list = HSUtils.getList(30);
				global_list_expiry = System.currentTimeMillis() + getCacheTimeLimit();
				break;
		}
	}
	
	@Override
	public Object getCachedData(Object... data) {
		String opcode = (String) data[0];
		boolean expired = hasExpired(data);
		if(expired)
			fetchNewData(data);
		switch(opcode) {
			case "personal":
				String username = (String) data[1];
				if(!userData.containsKey(username)) return null;
				return userData.get(username).getData();
			case "mini-list":
			case "global-list":
				return opcode.equals("mini-list") ? mini_list : global_list;
		}
		return null;
	}
	
	private boolean hasExpired(Object... data) {
		String opcode = (String) data[0];
		long time = 0;
		if(opcode.equals("personal")) {
			String username = (String) data[1];
			if(!userData.containsKey(username)) return true;
			time = (long) userData.get(username).getExpiry();
		} else
			time = opcode.equals("mini-list") ? mini_list_expiry : global_list_expiry;
		return time <= System.currentTimeMillis();
	}

	@Override
	public long getCacheTimeLimit() {
		return 5000;
	}
	
}
