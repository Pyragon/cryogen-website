package com.cryo.db.impl;

import java.sql.ResultSet;

import com.cryo.Website;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.modules.account.Account;
import com.cryo.utils.BCrypt;
import com.cryo.utils.Utilities;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:54:51 PM
 */
public class AccountConnection extends DatabaseConnection {
	
	public AccountConnection() {
		super("cryogen_global");
	}
	
	public static AccountConnection connection() {
		return (AccountConnection) Website.instance().getConnectionManager().getConnection(Connection.ACCOUNT);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
			case "register":
				String username = (String) data[1];
				String password = (String) data[2];
				String salt = BCrypt.generate_salt();
				String hash = BCrypt.hashPassword(password, salt);
				insert("player_data", username, password, salt, 0, 0);
				DisplayConnection.connection().handleRequest("create", username, Utilities.formatName(username));
				return new Object[] { true };
			case "compare":
				username = (String) data[1];
				password = (String) data[2];
				ResultSet set = select("player_data", "username='"+username+"'");
				if(empty(set))
					return null;
				salt = getString(set, "salt");
				hash = getString(set, "password");
				return new Object[] { BCrypt.hashPassword(password, salt).equals(hash) };
			case "get-account":
				username = (String) data[1];
				set = select("player_data", "username='"+username+"'");
				if(empty(set))
					return null;
				int rights = getInt(set, "rights");
				int donator = getInt(set, "donator");
				return new Object[] { new Account(username, rights, donator) };
		}
		return null;
	}
	
}
