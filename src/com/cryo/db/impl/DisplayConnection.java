package com.cryo.db.impl;

import java.sql.ResultSet;

import com.cryo.Website;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.DBConnectionManager.Connection;

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
			case "name-exists":
				String name = (String) data[1];
				name = name.toLowerCase().replaceAll(" ", "_");
				ResultSet set = select("current_names", "username='"+name+"' OR display_name='"+name+"'");
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
