package com.cryo.db.impl;

import java.sql.Timestamp;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
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
		RecoveryDAO recovery = new RecoveryDAO(id, username, email, forum, creation == null ? 0L : creation.getTime(), cico, additional, passes, status, new_pass);
		return new Object[] { recovery };
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
			case "add-email-rec":
			case "add-forum-rec":
				id = (String) data[1];
				String rand = (String) data[2];
				insert("instant", id, rand, opcode.contains("email") ? RecoveryModule.EMAIL : RecoveryModule.FORUM);
				break;
		}
		return null;
	}
	
}
