package com.cryo.managers;

import com.cryo.db.impl.AccountConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.account.entities.Account;
import spark.Request;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 14, 2017 at 1:58:17 AM
 */
public class CookieManager {
	
	//User loads page, if sess_id is present, find acc from id
	
	public static boolean isLoggedIn(Request request) {
		return getAccount(request) != null;
	}
	
	public static Account getAccount(Request request) {
		if(request.cookies().containsKey("cryo-sess")) {
			String sess_id = request.cookie("cryo-sess");
			Object[] data = AccountConnection.connection().handleRequest("get-user", sess_id);
			if(data == null) {
				request.cookies().remove("cryo-sess");
				return null;
			}
			String username = (String) data[0];
			Account account = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, username);
			return account;
		}
		return null;
	}
	
	public static int getRights(Request request) {
		if(!isLoggedIn(request)) return -1;
		Account account = getAccount(request);
		if(account == null) return -1;
		return account.getRights();
	}
	
	public static String getUsername(Request request) {
		Account account = getAccount(request);
		if(account == null)
			return "";
		return account.getUsername();
	}
	
}
