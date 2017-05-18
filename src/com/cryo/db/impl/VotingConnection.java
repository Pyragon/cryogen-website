package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.modules.account.vote.AuthDAO;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 12, 2017 at 11:19:51 AM
 */
public class VotingConnection extends DatabaseConnection {

	public VotingConnection() {
		super("cryogen_vote");
	}
	
	public static VotingConnection connection() {
		return (VotingConnection) Website.instance().getConnectionManager().getConnection(Connection.VOTING);
	}
	
	private final SQLQuery GET_AUTHS = (set) -> {
		if(wasNull(set))
			return null;
		ArrayList<AuthDAO> auths = new ArrayList<>();
		while(next(set)) {
			int id = getInt(set, "ID");
			String username = getString(set, "username");
			String auth = getString(set, "auth");
			Timestamp timestamp = getTimestamp(set, "timestamp");
			auths.add(new AuthDAO(id, username, auth, timestamp));
		}
		return new Object[] { auths };
	};
	
	private final SQLQuery GET_SITES = (set) -> {
		if(empty(set)) return null;
		HashMap<String, Timestamp> sites = new HashMap<String, Timestamp>() {
			private static final long serialVersionUID = 1L;

		{
			put("runelocus", getTimestamp(set, "runelocus"));
			put("rune-server", getTimestamp(set, "rune-server"));
			put("toplist", getTimestamp(set, "toplist"));
		}};
		return new Object[] { sites };
	};

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "remove-auth":
				String auid = (String) data[1];
				try {
					delete("auths", "ID="+auid);
					return new Object[] { true };
				} catch(Exception e) {
					return new Object[] { false };
				}
			case "get-auths":
				String username = (String) data[1];
				data = select("auths", "username=?", GET_AUTHS, username);
				return data == null ? null : new Object[] { (ArrayList<AuthDAO>) data[0] };
			case "get-time":
				username = (String) data[1];
				String site = (String) data[2];
				data = select("vote_data", "username=?", GET_SITES, username);
				return data == null ? null : new Object[] { ((HashMap<String, Timestamp>) data[0]).get(site) };
		}
		return null;
	}
	
}
