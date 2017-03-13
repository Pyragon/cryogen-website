package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import com.cryo.Website;
import com.cryo.db.DatabaseConnection;
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
				username = (String) data[1];
				Date date = new Date();
				ResultSet set = select("delays", "username='"+username+"'");
				if(empty(set))
					return new Object[] { 0 };
				Timestamp stamp = getTimestamp(set, "timestamp");
				if(date.getTime() > stamp.getTime())
					return new Object[] { 0 };
				long diff = stamp.getTime() - date.getTime();
				long seconds = diff / 1000;
				return new Object[] { (int) seconds };
			case "change-display":
				username = (String) data[1];
				display = (String) data[2];
				String next = (String) data[3];
				data = handleRequest("get-last-display", username);
				if(data != null)
					delete("last_names", "username='"+username+"'");
				if(!Utilities.formatNameForProtocol(display).equals(username))
					insert("last_names", username, display);
				set = select("delays", "username='"+username+"'");
				Calendar c = Calendar.getInstance();
				c.add(Calendar.HOUR, 7*24);
				long time = c.getTimeInMillis();
				if(empty(set))
					insert("delays", username, time);
				else
					set("delays", "timestamp=DEFAULT", "username='"+username+"'");
				set("current_names", "display_name='"+next+"'", "username='"+username+"'");
				break;
			case "get-last-display":
				username = (String) data[1];
				set = select("last_names", "username='"+username+"'");
				if(set == null || wasNull(set) || !next(set))
					return null;
				return new Object[] { getString(set, "display_name") };
			case "name-exists":
				String name = (String) data[1];
				name = name.toLowerCase().replaceAll(" ", "_");
				set = select("current_names", "username='"+name+"' OR display_name='"+name+"'");
				if(!empty(set))
					return new Object[] { true };
				set = select("last_names", "username='"+name+"' OR display_name='"+name+"'");
				return new Object[] { !empty(set) };
			case "get-username":
				name = (String) data[1];
				set = select("current_names", "display_name LIKE '"+name+"'");
				if(empty(set))
					return null;
				return new Object[] { getString(set, "username") };
			case "get-display":
				name = (String) data[1];
				set = select("current_names", "username='"+name+"'");
				if(empty(set))
					return null;
				return new Object[] { getString(set, "display_name") };
		}
		return null;
	}
	
}
