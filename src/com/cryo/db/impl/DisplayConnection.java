package com.cryo.db.impl;

import java.sql.ResultSet;

import com.cryo.db.DatabaseConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 10:03:45 PM
 */
public class DisplayConnection extends DatabaseConnection {

	public DisplayConnection() {
		super("cryogen_display");
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
			case "get-username":
				String name = (String) data[1];
				ResultSet set = select("current_names", "display_name='"+name+"'");
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
