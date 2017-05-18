package com.cryo.db.impl;

import java.util.ArrayList;

import com.cryo.Website;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.modules.account.AccountDAO;
import com.cryo.utils.BCrypt;
import com.cryo.utils.CookieManager;
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

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
			case "get-misc-data":
				String name = (String) data[1];
				data = select("misc_data", "name=?", GET_MISC_DATA, name);
				if(data == null) return null;
				return new Object[] { data[0] };
			case "register":
				String username = (String) data[1];
				String password = (String) data[2];
				String salt = BCrypt.generate_salt();
				String hash = BCrypt.hashPassword(password, salt);
				String sess_id = CookieManager.generateSessId(username, password, salt);
				insert("player_data", "DEFAULT", username, password, salt, sess_id, 0, 0);
				DisplayConnection.connection().handleRequest("create", username, Utilities.formatName(username));
				return new Object[] { true };
			case "search":
				String text = (String) data[1];
				text = "%"+text+"%";
				data = select("player_data", "username LIKE ?", SEARCH_ACCOUNTS, text);
				if(data == null) return null;
				ArrayList<String> list = (ArrayList<String>) data[0];
				ArrayList<AccountDAO> accounts = new ArrayList<>();
				for(String user : list) {
					data = handleRequest("get-account", user);
					if(data == null) continue;
					accounts.add((AccountDAO) data[0]);
				}
				return new Object[] { accounts };
			case "get-acc-from-sess":
				sess_id = (String) data[1];
				data = select("player_data", "sess_id=?", GET_ACCOUNT, sess_id);
				return data == null ? null : new Object[] { ((AccountDAO) data[0]) };
			case "compare":
				username = (String) data[1];
				password = (String) data[2];
				data = select("player_data", "username=?", GET_HASH_PASS, username);
				if(data == null)
					return null;
				hash = (String) data[0];
				salt = (String) data[1];
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
				AccountDAO account = (AccountDAO) data[1];
				data = select("player_data", "username=?", GET_HASH_PASS, account.getUsername());
				if(data == null)
					return null;
				hash = (String) data[0];
				salt = (String) data[1];
				String toHash = String.format("%s%s%s", account.getUsername(), hash, salt);
				sess_id = CookieManager.hashSessId(toHash);
				return new Object[] { sess_id };
			case "get-account":
				username = (String) data[1];
				data = select("player_data", "username=?", GET_ACCOUNT, username);
				return data == null ? null : new Object[] { ((AccountDAO) data[0]) };
		}
		return null;
	}
	
	private final SQLQuery GET_MISC_DATA = (set) -> {
		if(empty(set))
			return null;
		return new Object[] { getString(set, "value") };
	};
	
	private final SQLQuery GET_HASH_PASS = (set) -> {
		if(empty(set))
			return null;
		String hash = getString(set, "password");
		String salt = getString(set, "salt");
		return new Object[] { hash, salt };
	};
	
	private final SQLQuery GET_ACCOUNT = (set) -> {
		if(empty(set))
			return null;
		String username = getString(set, "username");
		int id = getInt(set, "id");
		int rights = getInt(set, "rights");
		int donator = getInt(set, "donator");
		AccountDAO account = new AccountDAO(username, id, rights, donator);
		return new Object[] { account };
	};
	
	private final SQLQuery SEARCH_ACCOUNTS = (set) -> {
		ArrayList<String> accounts = new ArrayList<String>();
		if(wasNull(set))
			return null;
		while(next(set)) {
			String username = getString(set, "username");
			accounts.add(username);
		}
		return new Object[] { accounts };
	};
	
}
