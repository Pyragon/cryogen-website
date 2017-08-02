package com.cryo.db.impl;

import java.sql.Timestamp;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.modules.account.recovery.InstantRecoveryDAO;
import com.cryo.modules.account.recovery.RecoveryDAO;
import com.cryo.modules.account.recovery.RecoveryModule;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: July 19, 2017 at 4:08:22 AM
 */
public class RecoveryConnection extends DatabaseConnection {

	public RecoveryConnection() {
		super("cryogen_recovery");
	}
	
	public static RecoveryConnection connection() {
		return (RecoveryConnection) Website.instance().getConnectionManager().getConnection(Connection.RECOVERY);
	}
	
	private final SQLQuery GET_RECOVERY = (set) -> {
		if(empty(set)) return null;
		String id = getString(set, "id");
		String username = getString(set, "username");
		String email = getString(set, "email");
		String forum = getString(set, "forum");
		Timestamp creation = getTimestamp(set, "creation");
		String cico = getString(set, "cico");
		String additional = getString(set, "additional");
		int[] passes = new int[] { getInt(set, "pass0"), getInt(set, "pass1"), getInt(set, "pass2") };
		int status = getInt(set, "status");
		String new_pass = getString(set, "new_pass");
		String reason = getString(set, "reason");
		RecoveryDAO recovery = new RecoveryDAO(id, username, email, forum, creation == null ? 0L : creation.getTime(), cico, additional, passes, status, new_pass, reason);
		return new Object[] { recovery };
	};
	
	private final SQLQuery GET_INSTANT = (set) -> {
		if(empty(set)) return null;
		String id = getString(set, "id");
		String rand = getString(set, "rand");
		int method = getInt(set, "method");
		int status = getInt(set, "status");
		return new Object[] { new InstantRecoveryDAO(id, rand, method, status) };
	};

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "get-recovery":
				String id = (String) data[1];
				return select("recoveries", "id=?", GET_RECOVERY, id);
			case "add-recovery":
				RecoveryDAO recovery = (RecoveryDAO) data[1];
				insert("recoveries", recovery.data());
				break;
			case "set-status":
				id = (String) data[1];
				int status = (int) data[2];
				if(status == 2) {
					String new_pass = (String) data[3];
					set("recoveries", "status=?,new_pass=?", "id=?", status, new_pass, id);
					break;
				}
				set("recoveries", "status=?,reason=?", "id=?", status, (String) data[3], id);
				break;
			case "has-email-rec":
			case "has-forum-rec":
				id = (String) data[1];
				return select("instant", "id=? AND method=?", GET_INSTANT, id, opcode.contains("email") ? RecoveryModule.EMAIL : RecoveryModule.FORUM);
			case "set-email-status":
			case "set-forum-status":
				id = (String) data[1];
				status = (int) data[2];
				data = handleRequest("has-"+(opcode.contains("email") ? "email" : "forum")+"-rec", id);
				if(data == null) return null;
				set("instant", "status=?", "id=? AND method=?", status, id, opcode.contains("email") ? RecoveryModule.EMAIL : RecoveryModule.FORUM);
				break;
			case "add-email-rec":
			case "add-forum-rec":
				id = (String) data[1];
				String rand = (String) data[2];
				insert("instant", id, rand, opcode.contains("email") ? RecoveryModule.EMAIL : RecoveryModule.FORUM, 0);
				break;
		}
		return null;
	}
	
}
