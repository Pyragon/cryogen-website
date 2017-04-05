package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.modules.account.support.BugReportDAO;
import com.cryo.modules.account.support.PlayerReportDAO;
import com.google.gson.Gson;

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

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "get-player-reports":
				ResultSet set = select("player_reports", null);
				if(wasNull(set))
					return null;
				ArrayList<PlayerReportDAO> players = new ArrayList<>();
				while(next(set)) {
					int id = getInt(set, "id");
					String username = getString(set, "username");
					String title = getString(set, "title");
					String offender = getString(set, "offender");
					String rule = getString(set, "rule");
					String info = getString(set, "info");
					String proof = getString(set, "proof");
					String lastAction = getString(set, "last_action");
					String comment = getString(set, "comment");
					Timestamp time = getTimestamp(set, "time");
					String read = getString(set, "read");
					ArrayList<String> list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
					PlayerReportDAO report = new PlayerReportDAO(id, username, title, offender, rule, info, proof, lastAction, comment, time);
					report.setUsersRead(list);
					players.add(report);
				}
				return new Object[] { players };
			case "get-bug-reports":
				set = select("bug_reports", null);
				if(wasNull(set))
					return null;
				ArrayList<BugReportDAO> bugs = new ArrayList<>();
				while(next(set)) {
					int id = getInt(set, "id");
					String username = getString(set, "username");
					String title = getString(set, "title");
					String replicated = getString(set, "replicated");
					String date = getString(set, "seen");
					String info = getString(set, "info");
					String lastAction = getString(set, "last_action");
					String comment = getString(set, "comment");
					Timestamp time = getTimestamp(set, "time");
					String read = getString(set, "read");
					ArrayList<String> list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
					BugReportDAO bug = new BugReportDAO(id, username, title, replicated, date, info, lastAction, comment, time);
					bug.setUsersRead(list);
					bugs.add(bug);
				}
				return new Object[] { bugs };
			case "report_player":
				Object report = (PlayerReportDAO) data[1];
				insert("player_reports", (Object[]) ((PlayerReportDAO) report).data());
				break;
			case "report_bug":
				report = (BugReportDAO) data[1];
				insert("bug_reports", (Object[]) ((BugReportDAO) report).data());
				break;
		}
		return null;
	}
	
}
