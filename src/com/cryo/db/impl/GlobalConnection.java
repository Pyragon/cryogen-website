package com.cryo.db.impl;

import java.sql.ResultSet;

import com.cryo.Website;
import com.cryo.cookies.CookieManager;
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
public class GlobalConnection extends DatabaseConnection {
	
	public GlobalConnection() {
		super("cryogen_global");
	}
	
	public static GlobalConnection connection() {
		return (GlobalConnection) Website.instance().getConnectionManager().getConnection(Connection.GLOBAL);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
			case "get-misc-data":
				String name = (String) data[1];
				ResultSet set = select("misc_data", "name=?", name);
				if(empty(set))
					return null;
				return new Object[] { getString(set, "value") };
			case "register":
				String username = (String) data[1];
				String password = (String) data[2];
				String salt = BCrypt.generate_salt();
				String hash = BCrypt.hashPassword(password, salt);
				String sess_id = CookieManager.generateSessId(username, password, salt);
				insert("player_data", username, password, salt, sess_id, 0, 0);
				DisplayConnection.connection().handleRequest("create", username, Utilities.formatName(username));
				return new Object[] { true };
			case "get-acc-from-sess":
				sess_id = (String) data[1];
				set = select("player_data", "sess_id=?", sess_id);
				if(empty(set))
					return null;
				username = getString(set, "username");
				return handleRequest("get-account", username);
			case "compare":
				username = (String) data[1];
				password = (String) data[2];
				set = select("player_data", "username=?", username);
				if(empty(set))
					return null;
				salt = getString(set, "salt");
				hash = getString(set, "password");
				return new Object[] { BCrypt.hashPassword(password, salt).equals(hash) };
			case "change-pass":
				username = (String) data[1];
				password = (String) data[2];
				String current = (String) data[3];
				data = handleRequest("compare", username, current);
				if(data == null)
					return new Object[] { false, "Invalid username." };
				boolean compare = (boolean) data[0];
				if(!compare)
					return new Object[] { false, "Invalid current password." };
				salt = BCrypt.generate_salt();
				hash = BCrypt.hashPassword(password, salt);
				sess_id = CookieManager.generateSessId(username, hash, salt);
				set("player_data", "salt=?,password=?,sess_id=?", "username=?", salt, hash, sess_id, username);
				return new Object[] { true };
			case "get-sess-id":
				Account account = (Account) data[1];
				set = select("player_data", "username=?", account.getUsername());
				if(empty(set))
					return null;
				hash = getString(set, "password");
				salt = getString(set, "salt");
				sess_id = CookieManager.hashSessId(account.getUsername()+""+hash+""+salt);
				return new Object[] { sess_id };
			case "get-account":
				username = (String) data[1];
				set = select("player_data", "username=?", username);
				if(empty(set))
					return null;
				int rights = getInt(set, "rights");
				int donator = getInt(set, "donator");
				account = new Account(username, rights, donator);
				return new Object[] { account };
		}
		return null;
	}
	
}
