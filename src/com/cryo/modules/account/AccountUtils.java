package com.cryo.modules.account;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.DisplayConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:57:12 PM
 */
public class AccountUtils {
	
	public static AccountDAO getAccount(String username) {
		GlobalConnection connection = (GlobalConnection) Website.instance().getConnectionManager().getConnection(Connection.GLOBAL);
		Object[] data = connection.handleRequest("get-account", username);
		if(data == null)
			return null;
		return (AccountDAO) data[0];
	}
	
	public static String getDisplayName(AccountDAO account) {
		DisplayConnection connection = (DisplayConnection) Website.instance().getConnectionManager().getConnection(Connection.DISPLAY);
		Object[] data = connection.handleRequest("get-display", account.getUsername());
		if(data == null)
			return null;
		return (String) data[0];
	}
	
	public static String crownHTML(AccountDAO account) {
		String colour = "";
		String img = "";
		String display = "";
		if(account.getRights() == 2) {
			colour = "#FF0000";
			img = "admin_ing.png";
		} else if(account.getRights() == 1) {
			colour = "#0174DF";
			img = "mod_ing.png";
		} else if(account.getDonator() == 3) {
			colour = "#98C7F3";
			img = "hroller_ing.png";
		} else if(account.getDonator() == 2) {
			colour = "#01A9DB";
			img = "sdonator_ing.png";
		} else if(account.getDonator() == 1) {
			colour = "#004300";
			img = "donator_ing.png";
		}
		if(colour != "")
			display += "<span style=\"color: "+colour+";\"><strong><img src=\"images/crowns/"+img+"\"/> ";
		display += getDisplayName(account);
		if(colour != "")
			display += "</span></strong>";
		return display;
	}
	
}
