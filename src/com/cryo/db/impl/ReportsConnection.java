package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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
		Timestamp date = getTimestamp(set, "date");
		Timestamp archived = getTimestamp(set, "archived");
		String archiver = getString(set, "archiver");
		boolean active = getInt(set, "active") == 1;
		PlayerReport report = new PlayerReport(id, username, title, offender, rule, info, proof, lastAction, commentList, date, archived, active);
		if(archiver != null)
			report.setArchiver(archiver);
		return report;
	}

	private final BugReport loadBugReport(ResultSet set) {
		int id = getInt(set, "id");
		String username = getString(set, "username");
		String title = getString(set, "title");
		String replicated = getString(set, "replicated");
		String dateSeen = getString(set, "seen");
		String info = getString(set, "info");
		String lastAction = getString(set, "last_action");
		int commentList = getInt(set, "comment_list");
		Timestamp date = getTimestamp(set, "date");
		Timestamp archived = getTimestamp(set, "archived");
		String archiver = getString(set, "archiver");
		boolean active = getInt(set, "active") == 1;
		BugReport report = new BugReport(id, username, title, replicated, dateSeen, info, lastAction, commentList, date, archived, active);
		if(archiver != null)
			report.setArchiver(archiver);
		return report;
	}

	@Override
	public Object[] handleRequest2(Object... data) {
		String opcode = (String) data[0];
		switch (opcode) {
		case "search-results":
			Properties queryValues = (Properties) data[1];
			boolean archived = (boolean) data[2];
			HashMap<String, String> params = (HashMap<String, String>) data[3];
			String username = (String) data[4];
			if(params == null) System.out.println("null");
			int type = Integer.parseInt(params.get("type"));
			String query = (String) queryValues.get("query");
			Object[] values = (Object[]) queryValues.get("values");
			query += " AND active="+(archived ? 0 : 1);
			if(username != null)
				query += " AND username LIKE ?";
			query += " ORDER BY time DESC";
			ArrayList<Object> vals = new ArrayList<Object>(Arrays.asList(values));
			if(username != null)
				vals.add(username);
			int total = 0;
			if(type == 0)
				total = selectCount("bug_reports", query, GET_BUG_REPORTS, vals.toArray());
			else if(type == 1)
				total = selectCount("player_repots", query, GET_PLAYER_REPORTS, vals.toArray());
			else if(type == 2) {
				String realQuery = "SELECT COUNT(*) FROM (SELECT id, type, time, last_action, username, title, active FROM player_reports UNION ALL SELECT id, type, time, last_action, username, title, active FROM bug_reports) a WHERE "+query;
				ResultSet set = executeQuery(realQuery, vals.toArray());
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
			username = (String) data[5];
			if(params == null) System.out.println("null");
			type = Integer.parseInt(params.get("type"));
			query = (String) queryValues.get("query");
			values = (Object[]) queryValues.get("values");
			if(page == 0)
				page = 1;
			int offset = (page - 1) * 10;
			query += " AND active="+(archived ? 0 : 1);
			if(username != null)
				query += " AND username LIKE ?";
			query += " ORDER BY time DESC";
			query += " LIMIT "+ offset + ",10";
			vals = new ArrayList<Object>(Arrays.asList(values));
			if(username != null)
				vals.add(username);
			ArrayList<Report> reports = new ArrayList<Report>();
			if(type == 0) {
				data = select("bug_reports", query, GET_BUG_REPORTS, vals.toArray());
				if(data != null)
					reports.addAll((ArrayList<BugReport>) data[0]);
			} else if(type == 1) {
				data = select("player_reports", query, GET_PLAYER_REPORTS, vals.toArray());
				if(data != null)
					reports.addAll((ArrayList<PlayerReport>) data[0]);
			} else if(type == 2) {
				String realQuery = "SELECT a.* FROM (SELECT id, type, time, username, last_action, title, active FROM player_reports UNION ALL SELECT id, type, time, username, last_action, title, active FROM bug_reports) a WHERE "+query;
				ResultSet set = executeQuery(realQuery, vals.toArray());
				if(wasNull(set))
					break;
				while(next(set)) {
					int reportType = getInt(set, "type");
					int id = getInt(set, "id");
					String comm = reportType == 0 ? "get-bug-report" : "get-player-report";
					data = handleRequest2(comm, id);
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
			username = (String) data[3];
			if (page == 0)
				page = 1;
			offset = (page - 1) * 10;
			StringBuilder builder = new StringBuilder();
			builder	.append("player_reports")
					.append(" WHERE active=")
					.append(archived ? "0" : "1")
					.append(username != null ? " AND username LIKE ?" : "")
					.append(" ORDER BY ")
					.append(archived ? "archived" : "date")
					.append(" DESC LIMIT " + offset + ",10");
			values = new Object[username == null ? 0 : 1];
			if(username != null)
				values[0] = username;
			return select(builder.toString(), null, GET_PLAYER_REPORTS, values);
		case "get-reports":
			String typeName = (String) data[1];
			page = (int) data[2];
			username = (String) data[3];
			archived = (boolean) data[4];
			if(page == 0)
				page = 1;
			offset = (page - 1) * 10;
			reports = new ArrayList<Report>();
			if(typeName.equalsIgnoreCase("all")) {
				//ResultSet set = executeQuery("SELECT b.id, b.type, b.time, p.id, p.type, p.time FROM bug_reports b JOIN player_reports p WHERE b.username = '"+username+"' AND p.username = '"+username+"' AND p.active="+(archived ? "0" : "1")+" AND b.active="+(archived ? "0" : "1")+" ORDER BY p.time, b.time DESC LIMIT "+offset+",10");
				values = new Object[username == null ? 2 : 3];
				values[0] = username == null ? archived ? 0 : 1 : username;
				values[1] = username == null ? offset : archived ? 0 : 1;
				if(username != null)
					values[2] = offset;
				ResultSet set = executeQuery("SELECT a.* FROM (SELECT id, type, date, username, title, active FROM player_reports UNION ALL SELECT id, type, date, username, title, active FROM bug_reports) a WHERE "+(username != null ? "username = ? AND " : "")+"active=? ORDER BY date DESC LIMIT ?,10", values);
				if(wasNull(set))
					break;
				while(next(set)) {
					int reportType = getInt(set, "type");
					int id = getInt(set, "id");
					String comm = reportType == 0 ? "get-bug-report" : "get-player-report";
					data = handleRequest2(comm, id);
					if(data == null)
						continue;
					Report report = (Report) data[0];
					reports.add(report);
				}
			} else if(typeName.equalsIgnoreCase("bugs")) {
				data = handleRequest2("get-bug-reports", archived, page, username);
				ArrayList<BugReport> brep = (ArrayList<BugReport>) data[0];
				reports.addAll(brep);
			} else {
				data = handleRequest2("get-player-reports", archived, page, username);
				ArrayList<PlayerReport> prep = (ArrayList<PlayerReport>) data[0];
				reports.addAll(prep);
			}
			return new Object[] { reports };
		case "set-last-action":
			int id = (int) data[1];
			String lastAction = (String) data[2];
			int reportType = (int) data[3];
			data = handleRequest2("get-"+(reportType == 0 ? "bug" : "player")+"-report", id);
			if(data == null) return null;
			set(reportType == 0 ? "bug_reports" : "player_reports", "last_action=?", "id=?", lastAction, id);
			return new Object[] {};
		case "get-total-results":
			typeName = (String) data[1];
			page = (int) data[2];
			username = (String) data[3];
			archived = (boolean) data[4];
			int count = 0;
			query = username == null ? "active=?" : "active=? AND username=?";
			values = new Object[username == null ? 1 : 2];
			values[0] = archived ? 0 : 1;
			if(username != null)
				values[1] = username;
			if(typeName.equals("all"))
				count = selectCount("(SELECT id, type, date, username, active FROM player_reports UNION ALL SELECT id, type, date, username, active FROM bug_reports) a", query, values);
			else if(typeName.equals("bugs"))
				count = selectCount("bug_reports", query, values);
			else
				count = selectCount("player_reports", query, values);
			return new Object[] { (int) Utilities.roundUp(count, 10) };
		case "get-bug-reports":
			archived = (boolean) data[1];
			page = (int) data[2];
			username = (String) data[3];
			if (page == 0)
				page = 1;
			offset = (page - 1) * 10;
			builder = new StringBuilder();
			builder	.append("bug_reports")
					.append(" WHERE active=")
					.append(archived ? "0" : "1")
					.append(username != null ? " AND username LIKE ?" : "")
					.append(" ORDER BY ")
					.append(archived ? "archived" : "date")
					.append(" DESC LIMIT " + offset + ",10");
			values = new Object[username == null ? 0 : 1];
			if(username != null)
				values[0] = username;
			return select(builder.toString(), null, GET_BUG_REPORTS, values);
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
			try {
				data = table.contains("bug") ? handleRequest2("get-bug-report", id) : handleRequest2("get-player-report", id);
				if(data == null) return null;
				set(table, "active=0,last_action=?,archiver=?,archived=DEFAULT", "id=?", "Archived by $for-name="+username+"$end", username, id);
			} catch(Exception e) {
				e.printStackTrace();
			}
			//submit comment
			break;
		}
		return null;
	}

}
