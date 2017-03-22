package com.cryo.db.impl;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 20, 2017 at 1:43:27 PM
 */
public class ReportsConnection extends DatabaseConnection {

	public ReportsConnection() {
		super("cryogen_reports");
	}
	
	public static ReportsConnection connection() {
		return (ReportsConnection) Website.instance().getConnectionManager().getConnection(Connection.REPORTS);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "report_player":
				String username = (String) data[1];
				String title = (String) data[2];
				String player = (String) data[3];
				String rule = (String) data[4];
				String info = (String) data[5];
				String proof = (String) data[6];
				insert("player_reports", username, player, title, rule, info, proof);
				break;
			case "report_bug":
				username = (String) data[1];
				title = (String) data[2];
				String replicated = (String) data[3];
				String date = (String) data[4];
				info = (String) data[5];
				insert("bug_reports", username, title, replicated, date, info);
				break;
		}
		return null;
	}
	
}
