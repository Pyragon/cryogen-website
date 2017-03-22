package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.modules.account.support.punish.PunishDAO;

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
			case "get-punishments":
				String username = (String) data[1];
				ArrayList<PunishDAO> punishments = new ArrayList<>();
				ResultSet set = select("punishments", "username=?", username);
				if(set == null || wasNull(set))
					return new Object[] { punishments };
				while(next(set)) {
					int id = getInt(set, "id");
					int type = getInt(set, "type");
					Timestamp date = getTimestamp(set, "date");
					Timestamp expiry = getTimestamp(set, "expiry");
					String punisher = getString(set, "punisher");
					String reason = getString(set, "reason");
					boolean active = getInt(set, "active") == 1;
					int appealId = getInt(set, "appeal_id");
					PunishDAO punish = new PunishDAO(id, username, type, date, expiry, punisher, reason, active, appealId);
					punishments.add(punish);
				}
				return new Object[] { punishments };
			case "get-appeal":
				int punishmentId = (int) data[1];
				return new Object[] { };
		}
		return null;
	}
	
}
