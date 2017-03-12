package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
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

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "get-auths":
				String username = (String) data[1];
				ResultSet set = select("auths", "username='"+username+"'");
				if(set == null || wasNull(set))
					return null;
				ArrayList<AuthDAO> auths = new ArrayList<>();
				while(next(set)) {
					int id = getInt(set, "ID");
					String auth = getString(set, "auth");
					Timestamp timestamp = getTimestamp(set, "timestamp");
					auths.add(new AuthDAO(id, username, auth, timestamp));
				}
				return new Object[] { auths };
			case "get-time":
				username = (String) data[1];
				String site = (String) data[2];
				set = select("vote_data", "username='"+username+"'");
				if(empty(set))
					return null;
				return new Object[] { getTimestamp(set, site) };
		}
		return null;
	}
	
}
