package com.cryo.cache.impl;

import com.cryo.cache.CachedItem;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.managers.api.ServerConnection;

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
		StringBuilder players = new StringBuilder();
		boolean sData = false;
		for(String name : names) {
			Account account = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, name);
			if(account == null)
				continue;
			if(sData)
				players.append(", ");
			players.append(AccountUtils.crownHTML(account));
		}
		this.cachedData = players.toString();
	}

	@Override
	public long getCacheTimeLimit() {
		return 5000;
	}
	
}
