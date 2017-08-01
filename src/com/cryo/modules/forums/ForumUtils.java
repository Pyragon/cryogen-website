package com.cryo.modules.forums;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.MyBBConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 8, 2017 at 5:40:25 AM
 */
public class ForumUtils {
	
	public static ForumUser getUser(String username) {
		MyBBConnection connection = (MyBBConnection) Website.instance().getConnectionManager().getConnection(Connection.MY_BB);
		Object[] data = connection.handleRequest("get-user", username);
		if(data == null)
			return null;
		return (ForumUser) data[0];
	}
	
	public static String crownUser(ForumUser user) {
		int usergroup = user.getUsergroup();
		int displaygroup = user.getDisplaygroup();
		if(usergroup != displaygroup && displaygroup != 0)
			usergroup = displaygroup;
		String display = "{username}";
		switch(usergroup) {
			case 4: //owner
				display = "<span style=\"color: #FF0000;\"><strong><img src=\"images/crowns/owner.png\"/> {username}</strong></span>";
				break;
			case 3: //admin
				display = "<span style=\"color: #FF0000;\"><img src=\"images/crowns/admin.gif\"/> <strong>{username}</strong></span>";
				break;
			case 6: //moderator
				display = "<span style=\"color: #0174DF;\"><strong><img src=\"images/crowns/mod.gif\"/> {username}</strong></span>";
				break;
			case 8: //high-roller
				display = "<span style=\"color: #98C7F3;\"><strong><img src=\"images/crowns/hroller_ing.png\"/> {username}</strong></span>";
				break;
			case 9: //super-donator
				display = "<span style=\"color: #01A9DB;\"><strong><img src=\"images/crowns/sdonator_ing.png\"/> {username}</strong></span>";
				break;
			case 10: //donator
				display = "<span style=\"color: #004300;\"><strong><img src=\"images/crowns/donator_ing.png\"/> {username}</strong></span>";
				break;
		}
		display = display.replace("{username}", user.getUsername());
		return display;
	}
	
}
