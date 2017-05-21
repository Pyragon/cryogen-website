package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
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
	
	private final SQLQuery GET_COMMENTS = (set) -> {
		if(wasNull(set))
			return null;
		ArrayList<CommentDAO> comments = new ArrayList<>();
		while(next(set)) {
			int id = getInt(set, "id");
			int report_id = getInt(set, "report_id");
			int report_type = getInt(set, "report_type");
			String username = getString(set, "username");
			String comment = getString(set, "comment");
			Timestamp time = getTimestamp(set, "time");
			comments.add(new CommentDAO(id, report_id, report_type, username, comment, time));
		}
		return new Object[] { comments };
	};
	
	private final SQLQuery GET_PLAYER_REPORT = (set) -> {
		if(empty(set))
			return null;
		return new Object[] { loadPlayerReport(set, containsRow(set, "archived")) };
	};
	
	private final SQLQuery GET_PLAYER_REPORTS = (set) -> {
		if(wasNull(set))
			return null;
		ArrayList<PlayerReportDAO> players = new ArrayList<>();
		boolean archived = containsRow(set, "archived");
		while(next(set)) {
			PlayerReportDAO report = loadPlayerReport(set, archived);
			players.add(report);
		}
		return new Object[] { players };
	};
	
	private final SQLQuery GET_BUG_REPORT = (set) -> {
		if(empty(set)) return null;
		return new Object[] { loadBugReport(set, containsRow(set, "archived")) };
	};
	
	private final SQLQuery GET_BUG_REPORTS = (set) -> {
		if(wasNull(set))
			return null;
		ArrayList<BugReportDAO> bugs = new ArrayList<>();
		boolean archived = containsRow(set, "archived");
		while(next(set)) {
			BugReportDAO bug = loadBugReport(set, archived);
			bugs.add(bug);
		}
		return new Object[] { bugs };
	};
	
	@SuppressWarnings("unchecked")
	private final PlayerReportDAO loadPlayerReport(ResultSet set, boolean archived) {
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
		if(archived)
			report.setArchived(getTimestamp(set, "archived"));
		return report;
	}
	
	@SuppressWarnings("unchecked")
	private final BugReportDAO loadBugReport(ResultSet set, boolean archived) {
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
		if(archived)
			bug.setArchived(getTimestamp(set, "archived"));
		return bug;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "get-comments":
				int report_id = (int) data[1];
				int type = (int) data[2];
				data = select("comments", "report_id=? AND report_type=?", GET_COMMENTS, report_id, type);
				return data == null ? null : new Object[] { (ArrayList<CommentDAO>) data[0] };
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
				data = select(archived ? "p_archive" : "player_reports", "id=?", GET_PLAYER_REPORT, id);
				return data == null ? null : new Object[] { (PlayerReportDAO) data[0] };
			case "get-player-reports":
				archived = (boolean) data[1];
				int page = (int) data[2];
				if(page == 0) page = 1;
				int offset = (page - 1) * 10;
				StringBuilder builder = new StringBuilder();
				builder.append(archived ? "p_archive" : "player_reports")
						.append(" ORDER BY ").append(archived ? "archived" : "time")
						.append(" DESC LIMIT "+offset+",10");
				data = select(builder.toString(), null, GET_PLAYER_REPORTS);
				return data == null ? null : new Object[] { (ArrayList<PlayerReportDAO>) data[0] };
			case "get-total-results":
				String table = (String) data[1];
				return new Object[] { selectCount(table, null) };
			case "get-bug-reports":
				archived = (boolean) data[1];
				page = (int) data[2];
				if(page == 0) page = 1;
				offset = (page - 1) * 10;
				builder = new StringBuilder();
				builder.append(archived ? "b_archive" : "bug_reports")
						.append(" ORDER BY ").append(archived ? "archived" : "time")
						.append(" DESC LIMIT "+offset+",10");
				data = select(builder.toString(), null, GET_BUG_REPORTS);
				ArrayList<BugReportDAO> reports = (ArrayList<BugReportDAO>) data[0];
				return data == null ? null : new Object[] { reports };
			case "get-bug-report":
				id = (int) data[1];
				archived = (boolean) data[2];
				data = select(archived ? "b_archive" : "bug_reports", "id=?", GET_BUG_REPORT, id);
				return data == null ? null : new Object[] { (BugReportDAO) data[0] };
			case "report_player":
				PlayerReportDAO report = (PlayerReportDAO) data[1];
				insert("player_reports", (Object[]) ((PlayerReportDAO) report).data());
				break;
			case "report_bug":
				BugReportDAO breport = (BugReportDAO) data[1];
				insert("bug_reports", (Object[]) ((BugReportDAO) breport).data());
				break;
			case "archive-report":
				id = (int) data[1];
				type = (int) data[2];
				username = (String) data[3];
				String archive = type == 1 ? "b_archive" : "p_archive";
				table = type == 1 ? "bug_reports" : "player_reports";
				execute("INSERT INTO "+archive+" SELECT *, NOW() AS archived FROM "+table+" WHERE id=?;", id);
				handleRequest("submit-com", id, type, "cryobot", "Report has been archived by $for-name="+username+"$end.");
				set(archive, "last_action=?", "id=?", "Report archived by $for-name="+username+"$end", id);
				delete(table, "id=?", id);
				break;
		}
		return null;
	}
	
}
