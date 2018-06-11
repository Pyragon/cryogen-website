package com.cryo.db.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.cryo.Website;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.utils.Utilities;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 10:03:45 PM
 */
public class DisplayConnection extends DatabaseConnection {

	public DisplayConnection() {
		super("cryogen_display");
	}
	
	public static DisplayConnection connection() {
		return (DisplayConnection) Website.instance().getConnectionManager().getConnection(Connection.DISPLAY);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
			case "create":
				String username = (String) data[1];
				String display = (String) data[2];
				insert("current_names", username, display);
				insert("last_names", username, display);
				break;
			case "get-time":
				data = select("delays", "username=?", GET_TIME, (String) data[1]);
				if(data == null) return null;
				return new Object[] { (int) data[0] };
			case "change-display":
				username = (String) data[1];
				display = (String) data[2];
				String next = (String) data[3];
				data = handleRequest("get-last-display", username);
				if(data != null)
					delete("last_names", "username='"+username+"'");
				if(!Utilities.formatNameForProtocol(display).equals(username))
					insert("last_names", username, display);
				data = handleRequest("get-time", username);
				Calendar c = Calendar.getInstance();
				c.add(Calendar.HOUR, 7*24);
				long time = c.getTimeInMillis();
				if(data == null)
					insert("delays", username, time);
				else
					set("delays", "timestamp=?", "username=?", time, username);
				System.out.println("hi");
				set("current_names", "display_name=?", "username=?", next, username);
				break;
			case "get-last-display":
				username = (String) data[1];
				data = select("last_names", "username=?", GET_LAST_DISPLAY, username);
				if(data == null) return null;
				return new Object[] { (String) data[0] };
			case "name-exists":
				String name = (String) data[1];
				username = (String) data[2];
				name = name.toLowerCase().replaceAll(" ", "_");
				data = select("current_names", "username=? OR display_name=?", GET_USER_DISPLAY, name, name);
				if(data != null)
					return new Object[] { true };
				data = select("last_names", "username=? OR display_name=?", GET_USER_DISPLAY, name, name);
				if(data != null)
					return new Object[] { !username.equals((String) data[0]) };
				return new Object[] { data != null };
			case "get-username":
				name = (String) data[1];
				data = select("current_names", "display_name LIKE ?", GET_USER_DISPLAY, name);
				if(data == null) return null;
				return new Object[] { (String) data[0] };
			case "get-display":
				name = (String) data[1];
				data = select("current_names", "username=?", GET_USER_DISPLAY, name);
				if(data == null) return null;
				return new Object[] { (String) data[1] };
			case "search":
				return select("current_names", "display_name LIKE ? LIMIT 6", GET_USERS, (String) data[1]);
		}
		return null;
	}
	
	private final SQLQuery GET_USERS = (set) -> {
		if(wasNull(set)) return null;
		ArrayList<String> users = new ArrayList<>();
		while(next(set))
			users.add(getString(set, "username"));
		return new Object[] { users };
	};
	
	private final SQLQuery GET_TIME = (set) -> {
		if(empty(set)) return null;
		Timestamp stamp = getTimestamp(set, "timestamp");
		Date date = new Date();
		if(date.getTime() > stamp.getTime())
			return new Object[] { 0 };
		long diff = stamp.getTime() - date.getTime();
		long seconds = diff / 1000;
		return new Object[] { (int) seconds };
	};
	
	private final SQLQuery GET_USER_DISPLAY = (set) -> {
		if(empty(set))
			return null;
		return new Object[] { getString(set, "username"), getString(set, "display_name") };
	};
	
	private final SQLQuery GET_LAST_DISPLAY = (set) -> {
		if(empty(set)) return null;
		return new Object[] { getString(set, "display_name") };
	};
	
}
