package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.modules.account.support.punish.ACommentDAO;
import com.cryo.modules.account.support.punish.AppealDAO;
import com.cryo.modules.account.support.punish.PunishDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.modules.staff.search.Filter;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 22, 2017 at 2:28:33 AM
 */
public class PunishmentConnection extends DatabaseConnection {

	public PunishmentConnection() {
		super("cryogen_punish");
	}
	
	public static PunishmentConnection connection() {
		return (PunishmentConnection) Website.instance().getConnectionManager().getConnection(Connection.PUNISH);
	}
	
	private final SQLQuery GET_PUNISHMENTS = (set) -> {
		if(wasNull(set)) return null;
		List<PunishDAO> punishments = new ArrayList<>();
		while(next(set)) {
			PunishDAO punish = loadPunishment(set);
			punishments.add(punish);
		}
		return new Object[] { punishments };
	};
	
	private final SQLQuery GET_APPEALS = (set) -> {
		if(wasNull(set)) return null;
		List<AppealDAO> appeals = new ArrayList<>();
		while(next(set)) {
			AppealDAO appeal = loadAppeal(set);
			appeals.add(appeal);
		}
		return new Object[] { appeals };
	};
	
	private final SQLQuery GET_PUNISHMENT = (set) -> {
		if(empty(set)) return null;
		return new Object[] { loadPunishment(set) };
	};
	
	private final SQLQuery GET_APPEAL = (set) -> {
		if(empty(set)) return null;
		return new Object[] { loadAppeal(set) };
	};
	
	private final SQLQuery GET_COMMENTS = (set) -> {
		ArrayList<ACommentDAO> cList = new ArrayList<>();
		if(wasNull(set))
			return new Object[] { cList };
		while(next(set)) {
			int id = getInt(set, "id");
			int appeal_id = getInt(set, "fid");
			String username = getString(set, "username");
			String comment = getString(set, "comment");
			Timestamp time = getTimestamp(set, "time");
			cList.add(new ACommentDAO(id, appeal_id, username, comment, time));
		}
		return new Object[] { cList };
	};

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "search-appeal":
			case "search-punish":
				boolean isAppeal = opcode.contains("appeal");
				Properties queryValues = (Properties) data[1];
				int page = (int) data[2];
				String query = (String) queryValues.get("query");
				Object[] values = (Object[]) queryValues.get("values");
				if(page == 0) page = 1;
				int offset = (page - 1) * 10;
				query += " LIMIT "+offset+",10";
				data = select(isAppeal ? "appeals" : "punishments", query, isAppeal ? GET_APPEALS : GET_PUNISHMENTS, values);
				return data == null ? null : new Object[] { isAppeal ? (ArrayList<AppealDAO>) data[0] : (ArrayList<PunishDAO>) data[0] };
			case "search-results-appeal":
			case "search-results-punish":
				isAppeal = opcode.contains("appeal");
				queryValues = (Properties) data[1];
				query = (String) queryValues.get("query");
				values = (Object[]) queryValues.get("values");
				int total = selectCount(isAppeal ? "appeals" : "punishments", query, values);
				return new Object[] { (int) Utilities.roundUp(total, 10) };
			case "get-punishments":
				String username = (String) data[1];
				boolean archived = (boolean) data[2];
				page = (int) data[3];
				if(page == 0) page = 1;
				offset = (page - 1) * 10;
				StringBuilder builder = new StringBuilder();
				if(username != null)
					builder.append("username=? AND ");
				if(archived)
					builder.append(" active=0 OR expiry < NOW()");
				else
					builder.append(" active!=0 AND (expiry IS NULL OR expiry > NOW())");
				builder.append(" ORDER BY date DESC LIMIT "+offset+",10");
				if(username != null)
					data = select("punishments", builder.toString(), GET_PUNISHMENTS, username);
				else
					data = select("punishments", builder.toString(), GET_PUNISHMENTS);
				return new Object[] { data == null ? new ArrayList<PunishDAO>() : (ArrayList<PunishDAO>) data[0] };
			case "get-total-results":
				String table = (String) data[1];
				isAppeal = table.contains("appeals");
				boolean archive = table.contains("-a");
				if(table.contains("-a"))
					table = table.replaceAll("-a", "");
				query = "";
				if(archive) {
					query+= "active!=0";
					if(!isAppeal)
						query+= " OR expiry > NOW()";
				} else {
					query += "active=0";
					if(!isAppeal)
						query += " AND (expiry IS NULL OR expiry < NOW())";
				}
				return new Object[] { selectCount(table, query) };
			case "get-punishment-from-appeal":
				int appealId = (int) data[1];
				data = select("punishments", "appeal_id=?", GET_PUNISHMENT, appealId);
				if(data == null) return null;
				return new Object[] { (PunishDAO) data[0] };
			case "get-punishment":
				int id = (int) data[1];
				if(id == 0)
					return null;
				data = select("punishments", "id=?", GET_PUNISHMENT, id);
				if(data == null) return null;
				return new Object[] { (PunishDAO) data[0] };
			case "create-punishment":
				PunishDAO punish = (PunishDAO) data[1];
				try {
					insert("punishments", punish.data());
					return new Object[] { };
				} catch(Exception e) {
					return null;
				}
			case "extend-punishment":
				Timestamp expiry = (Timestamp) data[1];
				id = (int) data[2];
				data = new Object[expiry == null ? 1 : 2];
				data[0] = expiry == null ? id : expiry;
				if(expiry != null)
					data[1] = id;
				set("punishments", "expiry="+(expiry == null ? "NULL" : "?"), "id=?", data);
				break;
			case "create-appeal":
				AppealDAO appeal = (AppealDAO) data[1];
				int appeal_id = insert("appeals", appeal.data());
				set("punishments", "appeal_id=?", "id=?", appeal_id, appeal.getPunishId());
				break;
			case "get-appeals":
				archived = (boolean) data[1];
				page = (int) data[2];
				if(page == 0) page = 1;
				offset = (page - 1) * 10;
				ArrayList<AppealDAO> appeals = new ArrayList<>();
				data = select("appeals", (archived ? "active!=?" : "active=?")+" ORDER BY time DESC LIMIT "+offset+",10", GET_APPEALS, 0);
				return new Object[] { data == null ? appeals : (ArrayList<AppealDAO>) data[0] };
			case "get-appeal":
				id = (int) data[1];
				if(id == 0)
					return null;
				data = select("appeals", "id=?", GET_APPEAL, id);
				return data == null ? null : new Object[] { (AppealDAO) data[0] };
			case "close-appeal":
				id = (int) data[1];
				int status = (int) data[2];
				username = (String) data[3];
				String reason = data.length == 5 ? (String) data[4] : "";
				if(status == 2 && reason.equals(""))
					return null;
				String action = "Appeal "+(status == 1 ? "accepted" : "declined")+" by $for-name="+username+"$end";
				set("appeals", "active=?, reason=?, action=?", "id=?", status, reason, action, id);
				if(status == 1) //accept, need to set punishment to inactive
					set("punishments", "active=0", "appeal_id=?", id);
				break;
			case "add-comment":
				username = (String) data[1];
				appealId = (int) data[2];
				int type = (int) data[3];
				String comment = (String) data[4];
				insert("comments", "DEFAULT", appealId, type, username, comment, "DEFAULT");
				if(type == 0)
					set("appeals", "action=?", "id=?", "Comment submitted by $for-name="+username+"$end", appealId);
				break;
			case "get-comments":
				appeal_id = (int) data[1];
				type = (int) data[2];
				ArrayList<ACommentDAO> cList = new ArrayList<>();
				if(appeal_id == 0)
					return new Object[] { cList };
				data = select("comments", "fid=? AND type=? ORDER BY time DESC", GET_COMMENTS, appeal_id, type);
				return data == null ? null : new Object[] { (ArrayList<ACommentDAO>) data[0] };
		}
		return null;
	}
	
	public PunishDAO loadPunishment(ResultSet set) {
		int id = getInt(set, "id");
		int type = getInt(set, "type");
		String username = getString(set, "username");
		Timestamp date = getTimestamp(set, "date");
		Timestamp expiry = getTimestamp(set, "expiry");
		String punisher = getString(set, "punisher");
		String reason = getString(set, "reason");
		boolean active = getInt(set, "active") == 1;
		int appealId = getInt(set, "appeal_id");
		PunishDAO punish = new PunishDAO(id, username, type, date, expiry, punisher, reason, active, appealId);
		AppealDAO appeal = new PunishUtils().getAppeal(appealId);
		if(appeal != null)
			punish.setAppeal(appeal);
		return punish;
	}
	
	@SuppressWarnings("unchecked")
	public AppealDAO loadAppeal(ResultSet set) {
		int id = getInt(set, "id");
		int type = getInt(set, "type");
		int punishId = getInt(set, "punish_id");
		String username = getString(set, "username");
		String title = getString(set, "title");
		String message = getString(set, "message");
		String reason = getString(set, "reason");
		String lastAction = getString(set, "action");
		int activei = getInt(set, "active");
		Timestamp time = getTimestamp(set, "time");
		AppealDAO appeal = new AppealDAO(id, type, username, title, message, activei, punishId, time);
		String read = getString(set, "read");
		ArrayList<String> list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
		if(!reason.equals(""))
			appeal.setReason(reason);
		if(!lastAction.equals(""))
			appeal.setLastAction(lastAction);
		appeal.setUsersRead(list);
		return appeal;
	}
	
}
