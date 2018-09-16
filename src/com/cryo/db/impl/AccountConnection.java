package com.cryo.db.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.security.SessionIDGenerator;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

public class AccountConnection extends DatabaseConnection {

	public AccountConnection() {
		super("cryogen_accounts");
	}
	
	public static AccountConnection connection() {
		if(Website.instance() == null || Website.instance().getConnectionManager() == null) return null;
		DatabaseConnection con = Website.instance().getConnectionManager().getConnection(Connection.ACCOUNT);
		if(con == null) return null;
		return (AccountConnection) con;
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
			case "add-token":
				String username = (String) data[1];
				long expiryL = (long) data[2];
				boolean revoke = (boolean) data[3];
				String token = SessionIDGenerator.getInstance().getSessionId();
				if(revoke)
					delete("tokens", "username=?", username);
				Timestamp stamp = new Timestamp(expiryL);
				insert("tokens", "DEFAULT", username, token, stamp);
				return new Object[] { token };
			case "get-user-from-token":
				token = (String) data[1];
				data = select("tokens", "token=?", GET_ACCOUNT, token);
				if(data == null) return null;
				username = (String) data[0];
				Timestamp expiry = (Timestamp) data[1];
				if(expiry.getTime() < System.currentTimeMillis()) return null;
				return new Object[] { username };
			case "remove-tokens":
				delete("tokens", "expiry < DATE_SUB(NOW(), INTERVAL 1 DAY);");
				break;
		case "get-user":
			String sess_id = (String) data[1];
			return select("sessions", "sess_id=?", GET_ACCOUNT_FROM_SESSION, sess_id);
		case "add-sess":
			username = (String) data[1];
			sess_id = SessionIDGenerator.getInstance().getSessionId();
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_YEAR, 30);
			stamp = new Timestamp(c.getTime().getTime());
			insert("sessions", username, sess_id, stamp);
			return new Object[] { sess_id };
		case "remove-all-sess":
			username = (String) data[1];
			delete("sessions", "username=?", username);
			break;
		}
		return null;
	}
	
	private final SQLQuery GET_ACCOUNT_FROM_SESSION = (set) -> {
		if(empty(set)) return null;
		String username = getString(set, "username");
		return new Object[] { username };
	};

	private final SQLQuery GET_ACCOUNT = (set) -> {
		if(empty(set)) return null;
		String username = getString(set, "username");
		Timestamp expiry = getTimestamp(set, "expiry");
		return new Object[] { username, expiry };
	};

}
