package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.modules.account.support.BugReportDAO;
import com.cryo.modules.account.support.PlayerReportDAO;
import com.cryo.utils.CommentDAO;
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
			case "get-comments":
				int report_id = (int) data[1];
				int type = (int) data[2];
				ResultSet set = select("comments", "report_id=? AND report_type=?", report_id, type);
				if(wasNull(set))
					return null;
				ArrayList<CommentDAO> comments = new ArrayList<>();
				while(next(set)) {
					int id = getInt(set, "id");
					String username = getString(set, "username");
					String comment = getString(set, "comment");
					Timestamp time = getTimestamp(set, "time");
					comments.add(new CommentDAO(id, report_id, type, username, comment, time));
				}
				return new Object[] { comments };
			case "submit-com":
				int id = (int) data[1];
				type = (int) data[2];
				String username = (String) data[3];
				String comment = (String) data[4];
				insert("comments", "DEFAULT", id, type, username, comment, "DEFAULT");
				String db = type == 0 ? "player_reports" : "bug_reports";
				set(db, "last_action=?", "id=?", "Comment submitted by $for-name="+username+"$end", id);
				return new Object[] { };
			case "get-player-report":
				id = (int) data[1];
				boolean archived = (boolean) data[2];
				set = select(archived ? "p_archive" : "player_reports", "id=?", id);
				if(empty(set))
					return null;
				username = getString(set, "username");
				String title = getString(set, "title");
				String offender = getString(set, "offender");
				String rule = getString(set, "rule");
				String info = getString(set, "info");
				String proof = getString(set, "proof");
				String lastAction = getString(set, "last_action");
				comment = getString(set, "comment");
				Timestamp time = getTimestamp(set, "time");
				String read = getString(set, "read");
				ArrayList<String> list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
				PlayerReportDAO report = new PlayerReportDAO(id, username, title, offender, rule, info, proof, lastAction, comment, time);
				report.setUsersRead(list);
				if(archived)
					report.setArchived(getTimestamp(set, "archived"));
				return new Object[] { report };
			case "get-player-reports":
				archived = (boolean) data[1];
				set = select(archived ? "p_archive" : "player_reports", null);
				if(wasNull(set))
					return null;
				ArrayList<PlayerReportDAO> players = new ArrayList<>();
				while(next(set)) {
					id = getInt(set, "id");
					username = getString(set, "username");
					title = getString(set, "title");
					offender = getString(set, "offender");
					rule = getString(set, "rule");
					info = getString(set, "info");
					proof = getString(set, "proof");
					lastAction = getString(set, "last_action");
					comment = getString(set, "comment");
					time = getTimestamp(set, "time");
					read = getString(set, "read");
					list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
					report = new PlayerReportDAO(id, username, title, offender, rule, info, proof, lastAction, comment, time);
					report.setUsersRead(list);
					if(archived)
						report.setArchived(getTimestamp(set, "archived"));
					players.add(report);
				}
				return new Object[] { players };
			case "get-bug-reports":
				archived = (boolean) data[1];
				set = select(archived ? "b_archive" : "bug_reports", null);
				if(wasNull(set))
					return null;
				ArrayList<BugReportDAO> bugs = new ArrayList<>();
				while(next(set)) {
					id = getInt(set, "id");
					username = getString(set, "username");
					title = getString(set, "title");
					String replicated = getString(set, "replicated");
					String date = getString(set, "seen");
					info = getString(set, "info");
					lastAction = getString(set, "last_action");
					comment = getString(set, "comment");
					time = getTimestamp(set, "time");
					read = getString(set, "read");
					list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
					BugReportDAO bug = new BugReportDAO(id, username, title, replicated, date, info, lastAction, comment, time);
					bug.setUsersRead(list);
					if(archived)
						bug.setArchived(getTimestamp(set, "archived"));
					bugs.add(bug);
				}
				return new Object[] { bugs };
			case "get-bug-report":
				id = (int) data[1];
				archived = (boolean) data[2];
				set = select(archived ? "b_archive" : "bug_reports", "id=?", id);
				if(empty(set))
					return null;
				id = getInt(set, "id");
				username = getString(set, "username");
				title = getString(set, "title");
				String replicated = getString(set, "replicated");
				String date = getString(set, "seen");
				info = getString(set, "info");
				lastAction = getString(set, "last_action");
				comment = getString(set, "comment");
				time = getTimestamp(set, "time");
				read = getString(set, "read");
				list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
				BugReportDAO bug = new BugReportDAO(id, username, title, replicated, date, info, lastAction, comment, time);
				bug.setUsersRead(list);
				if(archived)
					bug.setArchived(getTimestamp(set, "archived"));
				return new Object[] { bug };
			case "report_player":
				report = (PlayerReportDAO) data[1];
				insert("player_reports", (Object[]) ((PlayerReportDAO) report).data());
				break;
			case "report_bug":
				BugReportDAO breport = (BugReportDAO) data[1];
				insert("bug_reports", (Object[]) ((BugReportDAO) breport).data());
				break;
			case "archive-report":
				id = (int) data[1];
				type = (int) data[2];
				String archive = type == 0 ? "b_archive" : "p_archive";
				String table = type == 0 ? "bug_reports" : "player_reports";
				execute("INSERT INTO "+archive+" SELECT *, NOW() AS archived FROM "+table+" WHERE id=?;", id);
				delete(table, "id=?", id);
				break;
		}
		return null;
	}
	
}
