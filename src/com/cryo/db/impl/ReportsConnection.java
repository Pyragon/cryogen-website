package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.modules.account.entities.BugReport;
import com.cryo.modules.account.entities.PlayerReport;
import com.cryo.modules.account.entities.Report;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import lombok.Cleanup;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: March 20, 2017 at 1:43:27 PM
 */
public class ReportsConnection extends DatabaseConnection {

	public ReportsConnection() {
		super("cryogen_reports");
	}

	public static ReportsConnection connection() {
		return (ReportsConnection) Website	.instance()
											.getConnectionManager()
											.getConnection(Connection.REPORTS);
	}

	private final SQLQuery GET_PLAYER_REPORT = (set) -> {
		if (empty(set))
			return null;
		return new Object[] { loadPlayerReport(set) };
	};

	private final SQLQuery GET_PLAYER_REPORTS = (set) -> {
		if (wasNull(set))
			return null;
		ArrayList<PlayerReport> players = new ArrayList<>();
		while (next(set)) {
			PlayerReport report = loadPlayerReport(set);
			players.add(report);
		}
		return new Object[] { players };
	};

	private final SQLQuery GET_BUG_REPORT = (set) -> {
		if (empty(set))
			return null;
		return new Object[] { loadBugReport(set) };
	};

	private final SQLQuery GET_BUG_REPORTS = (set) -> {
		if (wasNull(set))
			return null;
		ArrayList<BugReport> bugs = new ArrayList<>();
		while (next(set)) {
			BugReport bug = loadBugReport(set);
			bugs.add(bug);
		}
		return new Object[] { bugs };
	};

	private final PlayerReport loadPlayerReport(ResultSet set) {
		int id = getInt(set, "id");
		String username = getString(set, "username");
		String title = getString(set, "title");
		String offender = getString(set, "offender");
		String rule = getString(set, "rule");
		String info = getString(set, "info");
		String proof = getString(set, "proof");
		int commentList = getInt(set, "comment_list");
		String lastAction = getString(set, "last_action");
		Timestamp time = getTimestamp(set, "time");
		Timestamp archived = getTimestamp(set, "archived");
		boolean active = getInt(set, "active") == 1;
		PlayerReport report = new PlayerReport(id, username, title, offender, rule, info, proof, lastAction, commentList, time, archived, active);
		return report;
	}

	private final BugReport loadBugReport(ResultSet set) {
		int id = getInt(set, "id");
		String username = getString(set, "username");
		String title = getString(set, "title");
		String replicated = getString(set, "replicated");
		String date = getString(set, "seen");
		String info = getString(set, "info");
		String lastAction = getString(set, "last_action");
		int commentList = getInt(set, "comment_list");
		Timestamp time = getTimestamp(set, "time");
		Timestamp archived = getTimestamp(set, "time");
		boolean active = getInt(set, "active") == 1;
		BugReport bug = new BugReport(id, username, title, replicated, date, info, lastAction, commentList, time, archived, active);
		return bug;
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch (opcode) {
		case "search-results":
			Properties queryValues = (Properties) data[1];
			boolean archived = (boolean) data[2];
			HashMap<String, String> params = (HashMap<String, String>) data[3];
			if(params == null) System.out.println("null");
			int type = Integer.parseInt(params.get("type"));
			String query = (String) queryValues.get("query");
			Object[] values = (Object[]) queryValues.get("values");
			query += " AND active="+(archived ? 0 : 1);
			query += " ORDER BY time DESC";
			int total = 0;
			if(type == 0)
				total = selectCount("bug_reports", query, GET_BUG_REPORTS, values);
			else if(type == 1)
				total = selectCount("player_repots", query, GET_PLAYER_REPORTS, values);
			else if(type == 2) {
				String realQuery = "SELECT COUNT(*) FROM (SELECT id, type, time, last_action, username, title, active FROM player_reports UNION ALL SELECT id, type, time, last_action, username, title, active FROM bug_reports) a WHERE "+query;
				ResultSet set = executeQuery(realQuery, values);
				try {
				if(!next(set)) {
					set.close();
					total = 0;
					return new Object[] { total };
				}
				total = getInt(set, 1);
				set.close();
				} catch(SQLException e) {
					e.printStackTrace();
					total = 0;
				}
			}
			return new Object[] { total };
		case "search":
			queryValues = (Properties) data[1];
			int page = (int) data[2];
			archived = (boolean) data[3];
			params = (HashMap<String, String>) data[4];
			if(params == null) System.out.println("null");
			type = Integer.parseInt(params.get("type"));
			query = (String) queryValues.get("query");
			values = (Object[]) queryValues.get("values");
			if(page == 0)
				page = 1;
			int offset = (page - 1) * 10;
			query += " AND active="+(archived ? 0 : 1);
			query += " ORDER BY time DESC";
			query += " LIMIT "+ offset + ",10";
			ArrayList<Report> reports = new ArrayList<Report>();
			if(type == 0) {
				data = select("bug_reports", query, GET_BUG_REPORTS, values);
				if(data != null)
					reports.addAll((ArrayList<BugReport>) data[0]);
			} else if(type == 1) {
				data = select("player_reports", query, GET_PLAYER_REPORTS, values);
				if(data != null)
					reports.addAll((ArrayList<PlayerReport>) data[0]);
			} else if(type == 2) {
				String realQuery = "SELECT a.* FROM (SELECT id, type, time, username, last_action, title, active FROM player_reports UNION ALL SELECT id, type, time, username, last_action, title, active FROM bug_reports) a WHERE "+query;
				ResultSet set = executeQuery(realQuery, values);
				if(wasNull(set))
					break;
				while(next(set)) {
					int reportType = getInt(set, "type");
					int id = getInt(set, "id");
					String comm = reportType == 0 ? "get-bug-report" : "get-player-report";
					data = handleRequest(comm, id);
					if(data == null)
						continue;
					Report report = (Report) data[0];
					reports.add(report);
				}
			}
			return new Object[] { reports };
		case "get-player-report":
			return select("player_reports", "id=?", GET_PLAYER_REPORT, (int) data[1]);
		case "get-player-reports":
			archived = (boolean) data[1];
			page = (int) data[2];
			if (page == 0)
				page = 1;
			offset = (page - 1) * 10;
			StringBuilder builder = new StringBuilder();
			builder	.append("player_reports")
					.append(" WHERE active=")
					.append(archived ? "0" : "1")
					.append(" ORDER BY ")
					.append(archived ? "archived" : "time")
					.append(" DESC LIMIT " + offset + ",10");
			return select(builder.toString(), null, GET_PLAYER_REPORTS);
		case "get-reports":
			String typeName = (String) data[1];
			page = (int) data[2];
			String username = (String) data[3];
			archived = (boolean) data[4];
			if(page == 0)
				page = 1;
			offset = (page - 1) * 10;
			reports = new ArrayList<Report>();
			if(typeName.equals("all")) {
				//ResultSet set = executeQuery("SELECT b.id, b.type, b.time, p.id, p.type, p.time FROM bug_reports b JOIN player_reports p WHERE b.username = '"+username+"' AND p.username = '"+username+"' AND p.active="+(archived ? "0" : "1")+" AND b.active="+(archived ? "0" : "1")+" ORDER BY p.time, b.time DESC LIMIT "+offset+",10");
				ResultSet set = executeQuery("SELECT a.* FROM (SELECT id, type, time, username, title, active FROM player_reports UNION ALL SELECT id, type, time, username, title, active FROM bug_reports) a WHERE username = ? AND active=? ORDER BY time DESC LIMIT ?,10", username, archived ? "0" : "1", offset);
				if(wasNull(set))
					break;
				while(next(set)) {
					int reportType = getInt(set, "type");
					int id = getInt(set, "id");
					String comm = reportType == 0 ? "get-bug-report" : "get-player-report";
					data = handleRequest(comm, id);
					if(data == null)
						continue;
					Report report = (Report) data[0];
					reports.add(report);
				}
			} else if(typeName.equals("bugs")) {
				data = handleRequest("get-bug-reports", archived, page);
				ArrayList<BugReport> brep = (ArrayList<BugReport>) data[0];
				reports.addAll(brep);
			} else {
				data = handleRequest("get-player-reports", archived, page);
				ArrayList<PlayerReport> prep = (ArrayList<PlayerReport>) data[0];
				reports.addAll(prep);
			}
			return new Object[] { reports };
		case "set-last-action":
			int id = (int) data[1];
			String lastAction = (String) data[2];
			int reportType = (int) data[3];
			data = handleRequest("get-"+(reportType == 0 ? "bug" : "player")+"-report", id);
			if(data == null) return null;
			set(reportType == 0 ? "bug_reports" : "player_reports", "last_action=?", "id=?", lastAction, id);
			return new Object[] {};
		case "get-total-results":
			typeName = (String) data[1];
			page = (int) data[2];
			username = (String) data[3];
			archived = (boolean) data[4];
			int count = 0;
			if(typeName.equals("all"))
				count = selectCount("(SELECT id, type, time, username, active FROM player_reports UNION ALL SELECT id, type, time, username, active FROM bug_reports) a", "active=? AND username=?", archived ? "0" : "1", username);
			else if(typeName.equals("bugs"))
				count = selectCount("bug_reports", "active=? AND username=?", archived ? "0" : "1", username);
			else
				count = selectCount("player_reports", "active=? AND username=?", archived ? "0" : "1", username);
			return new Object[] { (int) Utilities.roundUp(count, 10) };
		case "get-bug-reports":
			archived = (boolean) data[1];
			page = (int) data[2];
			if (page == 0)
				page = 1;
			offset = (page - 1) * 10;
			builder = new StringBuilder();
			builder	.append("bug_reports")
					.append(" WHERE active=")
					.append(archived ? "0" : "1")
					.append(" ORDER BY ")
					.append(archived ? "archived" : "time")
					.append(" DESC LIMIT " + offset + ",10");
			return select(builder.toString(), null, GET_BUG_REPORTS);
		case "get-bug-report":
			return select("bug_reports", "id=?", GET_BUG_REPORT, (int) data[1]);
		case "report-player":
			PlayerReport report = (PlayerReport) data[1];
			insert("player_reports", report.getCreationData());
			break;
		case "report-bug":
			BugReport breport = (BugReport) data[1];
			insert("bug_reports", breport.getCreationData());
			break;
		case "archive-report":
			id = (int) data[1];
			String table = (String) data[2];
			username = (String) data[3];
			Report rep = null;
			try {
				data = table.contains("bug") ? handleRequest("get-bug-report", id) : handleRequest("get-player-report", id);
				if(data == null) return null;
				rep = (Report) data[0];
				Website.instance().getCommentsManager().addComment("cryobot", "Report has been archived by $for-name="+username+"$end", rep.getCommentList());
				set(table, "active=0,last_action=?", "id=?", "Archived by $for-name="+username+"$end", id);
			} catch(Exception e) {
				e.printStackTrace();
			}
			//submit comment
			break;
		}
		return null;
	}

}
