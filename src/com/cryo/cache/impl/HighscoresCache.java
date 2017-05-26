package com.cryo.cache.impl;

import java.util.HashMap;

import com.cryo.cache.CachedItem;
import com.cryo.modules.highscores.HSDataList;
import com.cryo.modules.highscores.HSUtils;
import com.cryo.modules.highscores.HSUtils.HSData;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 24, 2017 at 5:03:42 AM
 */
public class HighscoresCache extends CachedItem {
	
	private HashMap<String, Object[]> userData;
	
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
			case "mini-list":
				mini_list = HSUtils.getList(10);
				mini_list_expiry = System.currentTimeMillis() + getCacheTimeLimit();
				break;
		}
	}
	
	@Override
	public Object getCachedData(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "personal":
				break;
			case "mini-list":
				if(hasExpired(data))
					fetchNewData(data);
				return mini_list;
			case "global-list":
				break;
		}
		return null;
	}
	
	private boolean hasExpired(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "personal":
				break;
			case "mini-list":
				return mini_list_expiry <= System.currentTimeMillis();
			case "global-list":
				break;
		}
		return true;
	}

	@Override
	public long getCacheTimeLimit() {
		return 5000;
	}
	
	public HSData getHighscoreData() {
		return null;
	}
	
}
