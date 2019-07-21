package com.cryo.db.impl;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.modules.account.recovery.InstantRecoveryDAO;
import com.cryo.modules.account.recovery.RecoveryModule;
import com.cryo.modules.staff.entities.Recovery;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: July 19, 2017 at 4:08:22 AM
 */
public class RecoveryConnection extends DatabaseConnection {

	public RecoveryConnection() {
		super("cryogen_recovery");
	}

	public static RecoveryConnection connection() {
		return (RecoveryConnection) Website.instance().getConnectionManager().getConnection(Connection.RECOVERY);
	}

	private final SQLQuery GET_RECOVERY = (set) -> {
		if (empty(set))
			return null;
		return new Object[] { getRecovery(set) };
	};

	private final SQLQuery GET_RECOVERIES = (set) -> {
		if (wasNull(set))
			return null;
		ArrayList<Recovery> recoveries = new ArrayList<>();
		while (next(set)) {
			Recovery recovery = getRecovery(set);
			if (recovery != null)
				recoveries.add(recovery);
		}
		return new Object[] { recoveries };
	};

	private final SQLQuery GET_INSTANT = (set) -> {
		if (empty(set))
			return null;
		String id = getString(set, "id");
		String rand = getString(set, "rand");
		int method = getInt(set, "method");
		int status = getInt(set, "status");
		return new Object[] { new InstantRecoveryDAO(id, rand, method, status) };
	};

	@SuppressWarnings("unchecked")
	private final Recovery getRecovery(ResultSet set) {
		String id = getString(set, "id");
		String username = getString(set, "username");
		String email = getString(set, "email");
        int forum = getInt(set, "forums");
		Timestamp creation = getTimestamp(set, "creation");
		String cico = getString(set, "cico");
		String additional = getString(set, "additional");
		int[] passes = new int[] { getInt(set, "pass0"), getInt(set, "pass1"), getInt(set, "pass2") };
		int status = getInt(set, "status");
		String new_pass = getString(set, "new_pass");
		String reason = getString(set, "reason");
		Timestamp date = getTimestamp(set, "date");
		String read = getString(set, "read");
		String ip = getString(set, "ip");
		ArrayList<String> list = read.equals("") ? new ArrayList<String>() : (ArrayList<String>) new Gson().fromJson(read, ArrayList.class);
		Recovery recovery = new Recovery(id, username, email, forum, creation == null ? 0L : creation.getTime(),
				cico, additional, passes, status, new_pass, reason, ip, date);
		if(recovery != null)
			recovery.setUsersRead(list);
		return recovery;
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch (opcode) {
		case "search-results":
			Properties queryValues = (Properties) data[1];
			boolean archived = (boolean) data[2];
			HashMap<String, String> params = (HashMap<String, String>) data[3];
			String username = (String) data[4];
			String module = (String) data[5];
			int type = -1;
			if(params.containsKey("type"))
				type = Integer.parseInt(params.get("type"));
			String query = (String) queryValues.get("query");
			Object[] values = (Object[]) queryValues.get("values");
			if(archived)
				query += " AND status!=0";
			else
				query += " AND status=0";
			query += " ORDER BY date DESC";
			return new Object[] { (int) Utilities.roundUp(selectCount(module, query, values), 10) };
		case "search":
			queryValues = (Properties) data[1];
			int page = (int) data[2];
			archived = (boolean) data[3];
			params = (HashMap<String, String>) data[4];
			username = (String) data[5];
			module = (String) data[6];
			query = (String) queryValues.get("query");
			values = (Object[]) queryValues.get("values");
			if (page == 0)
				page = 1;
			int offset = (page - 1) * 10;
			if(archived)
				query += " AND status!=0";
			else
				query += " AND status=0";
			query += " ORDER BY date DESC";
			query += " LIMIT "+offset+",10";
			return select(module, query, GET_RECOVERIES, values);
		case "get-recovery":
			String id = (String) data[1];
			return select("recoveries", "id=?", GET_RECOVERY, id);
		case "get-recoveries":
			username = (String) data[1];
			archived = (boolean) data[2];
			page = (int) data[3];
			if (page == 0)
				page = 1;
			offset = (page - 1) * 10;
			StringBuilder builder = new StringBuilder();
			if(username != null)
				builder.append(" username=? AND ");
			if (archived)
				builder.append(" status!=0");
			else
				builder.append(" status=0");
			builder.append(" ORDER BY date DESC LIMIT " + offset + ",10");
			values = new Object[username != null ? 1 : 0];
			values = new Object[username != null ? 1 : 0];
			if(username != null)
				values[0] = username;
			return select("recoveries", builder.toString(), GET_RECOVERIES, values);
		case "get-total-results":
			username = (String) data[1];
			archived = (boolean) data[2];
			builder = new StringBuilder();
			if(username != null)
				builder.append(" username=? AND ");
			if (archived)
				builder.append(" status!=0");
			else
				builder.append(" status=0");
			values = new Object[username != null ? 1 : 0];
			if(username != null)
				values[0] = username;
			return new Object[] { (int) Utilities.roundUp(selectCount("recoveries", builder.toString(), values), 10) };
		case "add-recovery":
			Recovery recovery = (Recovery) data[1];
			insert("recoveries", recovery.data());
			break;
		case "set-status":
			id = (String) data[1];
			int status = (int) data[2];
			if (status == 1) {
				String new_pass = (String) data[3];
				set("recoveries", "status=?,new_pass=?", "id=?", status, new_pass, id);
				break;
			}
			set("recoveries", "status=?,reason=?", "id=?", status, (String) data[3], id);
			break;
		case "has-email-rec":
            case "has-forums-rec":
			id = (String) data[1];
			return select("instant", "id=? AND method=? AND status=0", GET_INSTANT, id,
					opcode.contains("email") ? RecoveryModule.EMAIL : RecoveryModule.FORUM);
		case "set-email-status":
            case "set-forums-status":
			id = (String) data[1];
			status = (int) data[2];
                data = handleRequest("has-" + (opcode.contains("email") ? "email" : "forums") + "-rec", id);
			if (data == null)
				return null;
			set("instant", "status=?", "id=? AND method=?", status, id,
					opcode.contains("email") ? RecoveryModule.EMAIL : RecoveryModule.FORUM);
			break;
		case "add-email-rec":
            case "add-forums-rec":
			id = (String) data[1];
			String rand = (String) data[2];
			insert("instant", id, rand, opcode.contains("email") ? RecoveryModule.EMAIL : RecoveryModule.FORUM, 0, "DEFAULT");
			break;
		}
		return null;
	}

}
