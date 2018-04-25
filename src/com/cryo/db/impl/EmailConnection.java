package com.cryo.db.impl;

import java.sql.ResultSet;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;

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
		if(Website.instance() == null || Website.instance().getConnectionManager() == null)
			return null; //quick fix
		return (EmailConnection) Website.instance().getConnectionManager().getConnection(Connection.EMAIL);
	}
	
	public static EmailConnection connection(Website website) {
		return (EmailConnection) website.getConnectionManager().getConnection(Connection.EMAIL);
	}
	
	private final SQLQuery GET_USER_EMAIL = (set) -> {
		if(empty(set)) return null;
		return new Object[] { getString(set, "username"), getString(set, "email") };
	};

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
				delete("temp", "username=?", username);
				insert("temp", username, email, random, "DEFAULT");
				break;
			case "verify":
				random = (String) data[1];
				data = select("temp", "random=?", GET_USER_EMAIL, random);
				if(data == null) return null;
				username = (String) data[0];
				email = (String) data[1];
				delete("temp", "username=?", username);
				delete("linked", "username=?", username);
				insert("linked", username, email);
				return new Object[] { };
			case "get-email":
				username = (String) data[1];
				data = select("linked", "username=?", GET_USER_EMAIL, username);
				if(data == null) return null;
				return new Object[] { (String) data[1] };
		}
		return null;
	}
	
}
