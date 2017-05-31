package com.cryo.cache.impl;

import com.cryo.cache.CachedItem;
import com.cryo.modules.account.AccountDAO;
import com.cryo.modules.account.AccountUtils;
import com.cryo.server.ServerConnection;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 31, 2017 at 1:19:46 AM
 */
public class OnlineUserCache extends CachedItem {

	public OnlineUserCache() {
		super("online-user-cache");
	}

	@Override
	public void fetchNewData(Object... values) {
		String url = ServerConnection.SERVER_URL+"/online-users";
		String response = ServerConnection.getResponse(url);
		String[] names = response.split(",");
		String players = "";
		if(response.equals(""))
			this.cachedData =  "No one online at the moment.";
		boolean sData = false;
		for(String name : names) {
			AccountDAO account = AccountUtils.getAccount(name);
			if(account == null)
				continue;
			if(sData)
				players += ", ";
			players += AccountUtils.crownHTML(account);
		}
		this.cachedData = players;
	}

	@Override
	public long getCacheTimeLimit() {
		return 5000;
	}
	
}
