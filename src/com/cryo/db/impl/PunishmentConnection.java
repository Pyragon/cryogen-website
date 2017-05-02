package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.modules.account.support.punish.ACommentDAO;
import com.cryo.modules.account.support.punish.AppealDAO;
import com.cryo.modules.account.support.punish.PunishDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
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

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "archive":
				int id = (int) data[1];
				execute("INSERT INTO archive SELECT * FROM appeals WHERE id="+id);
				delete("appeals", "id=?", id);
				break;
			case "get-punishments":
				String username = (String) data[1];
				boolean archived = (boolean) data[2];
				ArrayList<PunishDAO> punishments = new ArrayList<>();
				ResultSet set = null;
				if(username != null)
					set = select("punishments", "username=?", username);
				else
					set = select("punishments", null);
				if(set == null || wasNull(set))
					return new Object[] { punishments };
				while(next(set)) {
					id = getInt(set, "id");
					int type = getInt(set, "type");
					String user = getString(set, "username");
					Timestamp date = getTimestamp(set, "date");
					Timestamp expiry = getTimestamp(set, "expiry");
					String punisher = getString(set, "punisher");
					String reason = getString(set, "reason");
					boolean active = getInt(set, "active") == 1;
					if(expiry != null && expiry.getTime() < System.currentTimeMillis())
						active = false;
					if((active && archived) || (!active && !archived))
						continue;
					int appealId = getInt(set, "appeal_id");
					PunishDAO punish = new PunishDAO(id, user, type, date, expiry, punisher, reason, active, appealId);
					AppealDAO appeal = new PunishUtils().getAppeal(appealId);
					if(appeal != null)
						punish.setAppeal(appeal);
					punishments.add(punish);
				}
				return new Object[] { punishments };
			case "get-punishment-from-appeal":
				int appealId = (int) data[1];
				set = select("punishments", "appeal_id=?", appealId);
				if(empty(set))
					return null;
				id = getInt(set, "id");
				int type = getInt(set, "type");
				username = getString(set, "username");
				Timestamp date = getTimestamp(set, "date");
				Timestamp expiry = getTimestamp(set, "expiry");
				String punisher = getString(set, "punisher");
				String reason = getString(set, "reason");
				boolean active = getInt(set, "active") == 1;
				PunishDAO punish = new PunishDAO(id, username, type, date, expiry, punisher, reason, active, appealId);
				AppealDAO appeal = new PunishUtils().getAppeal(appealId);
				if(appeal != null)
					punish.setAppeal(appeal);
				return new Object[] { punish };
			case "get-punishment":
				id = (int) data[1];
				if(id == 0)
					return null;
				set = select("punishments", "id=?", id);
				if(empty(set))
					return null;
				type = getInt(set, "type");
				username = getString(set, "username");
				date = getTimestamp(set, "date");
				expiry = getTimestamp(set, "expiry");
				punisher = getString(set, "punisher");
				reason = getString(set, "reason");
				active = getInt(set, "active") == 1;
				appealId = getInt(set, "appeal_id");
				punish = new PunishDAO(id, username, type, date, expiry, punisher, reason, active, appealId);
				appeal = new PunishUtils().getAppeal(appealId);
				if(appeal != null)
					punish.setAppeal(appeal);
				return new Object[] { punish };
			case "create-punishment":
				punish = (PunishDAO) data[1];
				try {
					insert("punishments", punish.data());
					return new Object[] { };
				} catch(Exception e) {
					return null;
				}
			case "extend-punishment":
				expiry = (Timestamp) data[1];
				id = (int) data[2];
				data = new Object[expiry == null ? 1 : 2];
				data[0] = expiry == null ? id : expiry;
				if(expiry != null)
					data[1] = id;
				set("punishments", "expiry="+(expiry == null ? "NULL" : "?"), "id=?", data);
				break;
			case "create-appeal":
				appeal = (AppealDAO) data[1];
				int appeal_id = insert("appeals", appeal.data());
				set("punishments", "appeal_id="+appeal_id, "id="+appeal.getPunishId());
				break;
			case "get-appeals":
				archived = (boolean) data[1];
				ArrayList<AppealDAO> appeals = new ArrayList<>();
				set = select("appeals", null);
				if(wasNull(set))
					return null;
				while(next(set)) {
					id = getInt(set, "id");
					int punishId = getInt(set, "punish_id");
					username = getString(set, "username");
					String title = getString(set, "title");
					String message = getString(set, "message");
					reason = getString(set, "reason");
					String lastAction = getString(set, "action");
					int activei = getInt(set, "active");
					Timestamp time = getTimestamp(set, "time");
					String read = getString(set, "read");
					ArrayList<String> list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
					appeal = new AppealDAO(id, username, title, message, activei, punishId, time);
					if(!reason.equals(""))
						appeal.setReason(reason);
					if(!lastAction.equals(""))
						appeal.setLastAction(lastAction);
					appeal.setUsersRead(list);
					if((!archived && activei != 0) || (archived && activei == 0))
						continue;
					appeals.add(appeal);
				}
				return new Object[] { appeals };
			case "get-appeal":
				id = (int) data[1];
				if(id == 0)
					return null;
				set = select("appeals", "id=?", id);
				if(empty(set))
					return null;
				id = getInt(set, "id");
				int punishId = getInt(set, "punish_id");
				username = getString(set, "username");
				String title = getString(set, "title");
				String message = getString(set, "message");
				reason = getString(set, "reason");
				String lastAction = getString(set, "action");
				int activei = getInt(set, "active");
				Timestamp time = getTimestamp(set, "time");
				appeal = new AppealDAO(id, username, title, message, activei, punishId, time);
				String read = getString(set, "read");
				ArrayList<String> list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
				if(!reason.equals(""))
					appeal.setReason(reason);
				if(!lastAction.equals(""))
					appeal.setLastAction(lastAction);
				appeal.setUsersRead(list);
				return new Object[] { appeal };
			case "close-appeal":
				id = (int) data[1];
				int status = (int) data[2];
				username = (String) data[3];
				reason = data.length == 5 ? (String) data[4] : "";
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
				type = (int) data[3];
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
				set = select("comments", "fid=? AND type=? ORDER BY time DESC", appeal_id, type);
				if(wasNull(set))
					return new Object[] { cList };
				while(next(set)) {
					id = getInt(set, "id");
					username = getString(set, "username");
					comment = getString(set, "comment");
					time = getTimestamp(set, "time");
					cList.add(new ACommentDAO(id, appeal_id, username, comment, time));
				}
				return new Object[] { cList };
		}
		return null;
	}
	
}
