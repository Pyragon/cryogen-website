package com.cryo.db.impl;

import java.sql.ResultSet;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 13, 2017 at 7:15:22 AM
 */
public class EmailConnection extends DatabaseConnection {
	
	public EmailConnection() {
		super("cryogen_email");
	}
	
	public static EmailConnection connection() {
		return (EmailConnection) Website.instance().getConnectionManager().getConnection(Connection.EMAIL);
	}
	
	public static EmailConnection connection(Website website) {
		return (EmailConnection) website.getConnectionManager().getConnection(Connection.EMAIL);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "remove-verifications":
				delete("temp", "expiry < DATE_SUB(NOW(), INTERVAL 1 DAY);");
				break;
			case "add-verify":
				String username = (String) data[1];
				String email = (String) data[2];
				String random = (String) data[3];
				insert("temp", username, email, random, "DEFAULT");
				break;
			case "verify":
				random = (String) data[1];
				ResultSet set = select("temp", "random=?", random);
				if(empty(set))
					return null;
				username = getString(set, "username");
				email = getString(set, "email");
				insert("linked", username, email);
				delete("temp", "username='"+username+"'");
				return new Object[] { };
			case "get-email":
				username = (String) data[1];
				set = select("linked", "username=?", username);
				if(empty(set))
					return null;
				return new Object[] { getString(set, "email") };
		}
		return null;
	}
	
}
