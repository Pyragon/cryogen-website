package com.cryo.db;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.db.impl.AccountConnection;
import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.EmailConnection;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.HighscoresConnection;
import com.cryo.db.impl.VotingConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 7:35:35 PM
 */
public class DBConnectionManager {
	
	private HashMap<Connection, DatabaseConnection> connections;
	
	public DBConnectionManager() {
		init();
		Website.LOADED = true;
	}
	
	public DatabaseConnection getConnection(Connection connection) {
		return connections.get(connection);
	}
	
	public void init() {
		connections = new HashMap<>();
		connections.put(Connection.FORUMS, new ForumConnection());
		connections.put(Connection.HIGHSCORES, new HighscoresConnection());
		connections.put(Connection.ACCOUNT, new AccountConnection());
		connections.put(Connection.DISPLAY, new DisplayConnection());
		connections.put(Connection.VOTING, new VotingConnection());
		connections.put(Connection.EMAIL, new EmailConnection());
	}
	
	public static enum Connection {
		FORUMS, HIGHSCORES, ACCOUNT, DISPLAY, VOTING, EMAIL
	}
	
}
