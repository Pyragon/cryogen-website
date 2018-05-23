package com.cryo.cache.impl;

import com.cryo.cache.CachedItem;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.server.ServerConnection;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 31, 2017 at 1:19:46 AM
 */
public class OnlineUserCache extends CachedItem {

	public OnlineUserCache() {
		super("online-users-cache");
	}

	@Override
	public void fetchNewData(Object... values) {
		String url = ServerConnection.SERVER_URL+"/online-users";
		String response = "";//ServerConnection.getResponse(url);
		if(response.equals("")) {
			this.cachedData =  "No one online at the moment.";
			return;
		}
		String[] names = response.split(",");
		String players = "";
		boolean sData = false;
		for(String name : names) {
			Account account = AccountUtils.getAccount(name);
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
