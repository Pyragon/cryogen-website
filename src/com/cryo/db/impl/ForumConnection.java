package com.cryo.db.impl;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.utils.Utilities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: July 19, 2017 at 4:30:40 AM
 */
public class ForumConnection extends DatabaseConnection {
	
	public ForumConnection() {
		super("cryogen_forum");
	}
	
	public static ForumConnection connection() {
		return (ForumConnection) Website.instance().getConnectionManager().getConnection(Connection.FORUM);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch (opcode) {
		case "get-uid":
			String username = (String) data[1];
			data = select("forum_linked", "username=?", GET_USER_DATA, username);
			if(data == null) return null;
			return new Object[] { ((UserData) data[0]).getUID() };
		case "get-username":
			int forum_id = (int) data[1];
			data = select("forum_linked", "forum_id=?", GET_USER_DATA, forum_id);
			if(data == null) return null;
			return new Object[] { ((UserData) data[0]).getUsername() };
		case "add-verify":
			username = (String) data[1];
			forum_id = (int) data[2];
            String[] webData = sendForumMessage(forum_id, "Forum Integration", "Your forums verification is nearly complete! Reply #yes to this message to finish, or #no to cancel.[br][br]"
					+ "This message will expire in 24 hours and you will need to relink the account", "Forum Verification");
			if(webData == null)
				return null;
			String message = webData[0];
			if(message.toLowerCase().contains("<br />") || message.toLowerCase().contains("array")) {
				System.out.println(Arrays.toString(webData));
				return null;
			}
			delete("forum_verify", "username=? OR forum_id=?", username, forum_id);
			insert("forum_verify", username, forum_id, Integer.parseInt(message), "DEFAULT");
			return new Object[] { };
		}
		return null;
	}
	
	public static String[] sendForumMessage(int id, String subject, String message, String reason) {
		subject = StringEscapeUtils.escapeHtml4(subject);
		message = StringEscapeUtils.escapeHtml4(message);
		message = message.replaceAll("&", "%26");
        String path = Website.getProperties().getProperty("forums-path");
		String secret = Website.getProperties().getProperty("secret-key");
		secret = StringEscapeUtils.escapeHtml4(secret);
		reason = StringEscapeUtils.escapeHtml4(reason);
		String[] url = new String[] { "http", path, "/send_message.php", "uid="+id+"&subject="+subject+"&message="+message+"&reason="+reason+"&secret="+secret };
		String[] webData = Utilities.getWebsite(url);
		System.out.println(Arrays.toString(webData));
		return webData;
	}
	
	private final SQLQuery GET_USER_DATA = (set) -> {
		if(empty(set)) return null;
		int uid = getInt(set, "forum_id");
		String username = getString(set, "username");
		return new Object[] { new UserData(uid, username, -1, null) };
	};
	
	@AllArgsConstructor
	public static class UserData {
		
		private final @Getter int UID;
		private final @Getter String username;
		
		private final @Getter int primary;
		private final @Getter int[] usergroups;
	}
	
}
