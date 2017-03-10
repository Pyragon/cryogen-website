package com.cryo.db.impl;

import java.sql.ResultSet;

import com.cryo.Website;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.highscores.HSUser;
import com.cryo.modules.highscores.HSUserList;
import com.cryo.modules.highscores.HSUtils;
import com.cryo.modules.highscores.HSUtils.HSData;
import com.cryo.utils.Utilities;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:05:27 PM
 */
public class HighscoresConnection extends DatabaseConnection {
	
	public HighscoresConnection() {
		super("cryogen_global");
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
			case "get-rank":
				String username = (String) data[1];
				String skill = "skill_"+((int) data[2]);
				String query = "SELECT * FROM highscores ORDER BY "+skill+" DESC";
				int index = 0;
				int rank = 0;
				ResultSet set = executeQuery(query);
				if(set == null || wasNull(set))
					return null;
				while(next(set)) {
					index++;
					String hsname = getString(set, "username");
					if(hsname.equals(username)) {
						rank = index;
						break;
					}
				}
				return new Object[] { rank };
			case "get-hs-data":
				String name = (String) data[1];
				set = getHSData(name);
				if(set == null) {
					DisplayConnection connection = (DisplayConnection) Website.instance().getConnectionManager().getConnection(Connection.DISPLAY);
					Object[] disp = connection.handleRequest("get-username", name);
					if(disp == null)
						return null;
					name = (String) disp[0];
					set = getHSData(name);
					if(set == null)
						return null;
				}
				double totalXP = getDouble(set, "total_xp");
				int totalLevel = getInt(set, "total_level");
				int[] xp = new int[25];
				for(int i = 0; i < 25; i++)
					xp[i] = getInt(set, "skill_"+i);
				HSData hsdata = new HSData(totalXP, totalLevel, xp);
				hsdata.formatName(name);
				hsdata.setRank(HSUtils.getRankData(name));
				return new Object[] { hsdata };
			case "get-mini-list":
				set = selectAll("highscores", "ORDER BY total_level DESC, total_xp DESC LIMIT 10");
				if(set == null || wasNull(set))
					return null;
				HSUserList list = new HSUserList();
				index = 1;
				while(next(set)) {
					rank = index++;
					name = getString(set, "username");
					String total_level = Utilities.instance().formatLong(getInt(set, "total_level"));
					String total_xp = Utilities.instance().formatLong(getInt(set, "total_xp"));
					name = "$for-name="+name+"$end";
					list.add(new HSUser(rank, name, total_level, total_xp));
				}
				return new Object[] { list };
		}
		return null;
	}
	
	private ResultSet getHSData(String name) {
		ResultSet set = select("highscores", "username='"+name+"'");
		if(empty(set))
			return null;
		return set;
	}
	
}
