package com.cryo.db.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.security.SessionIDGenerator;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.google.gson.Gson;

public class AccountConnection extends DatabaseConnection {

	public AccountConnection() {
		super("cryogen_accounts");
	}
	
	public static AccountConnection connection() {
		return (AccountConnection) Website.instance().getConnectionManager().getConnection(Connection.ACCOUNT);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
		case "get-user":
			String sess_id = (String) data[1];
			return select("sessions", "sess_id=?", GET_ACCOUNT_FROM_SESSION, sess_id);
		case "add-sess":
			String username = (String) data[1];
			sess_id = SessionIDGenerator.getInstance().getSessionId();
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_YEAR, 30);
			Timestamp stamp = new Timestamp(c.getTime().getTime());
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

}
