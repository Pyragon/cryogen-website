package com.cryo.db.impl;

import java.sql.ResultSet;

import com.cryo.Website;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.highscores.HSDataList;
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
	
	private final SQLQuery GET_RANK = (set) -> {
		if(empty(set)) return null;
		int rank = getInt(set, 2);
		System.out.println(rank);
		return new Object[] { rank };
	};
	
	private final SQLQuery GET_HS_DATA = (set) -> {
		String username = getString(set, "username");
		double totalXP = getDouble(set, "total_xp");
		int totalLevel = getInt(set, "total_level");
		int[] xp = new int[25];
		for(int i = 0; i < 25; i++)
			xp[i] = getInt(set, "skill_"+i);
		HSData hsdata = new HSData(totalXP, totalLevel, 0, xp);
		hsdata.formatName(username);
		hsdata.setRank(HSUtils.getRankData(username));
		return new Object[] { hsdata };
	};
	
	private final SQLQuery GET_SKILL_LIST = (set) -> {
		if(wasNull(set))
			return null;
		HSDataList list = new HSDataList();
		int index = 1;
		while(next(set)) {
			int rank = index++;
			Double totalXP = getDouble(set, "total_xp");
			int totalLevel = getInt(set, "total_level");
			String name = getString(set, "username");
			int[] xp = new int[25];
			for(int i = 0; i < 25; i++)
				xp[i] = getInt(set, "skill_"+i);
			HSData hsdata = new HSData(totalXP, totalLevel, rank, xp);
			hsdata.formatName(name);
			hsdata.setRank(HSUtils.getRankData(name));
			list.add(hsdata);
		}
		return new Object[] { list };
	};

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
			case "get-rank":
				String username = (String) data[1];
				String skill = "skill_"+((int) data[2]);
				String query = "SELECT x.username, x.position FROM (SELECT t.username, t."+skill+", @rownum := @rownum + 1 AS position FROM `highscores` t JOIN (SELECT @rownum := 0) r ORDER BY total_level DESC, total_xp) x WHERE x.username = ?";
				data = getResults(query, GET_RANK, username);
				break;
			case "get-hs-data":
				String name = (String) data[1];
				data = select("highscores", "username=?", GET_HS_DATA, name);
				if(data == null) {
					DisplayConnection connection = (DisplayConnection) Website.instance().getConnectionManager().getConnection(Connection.DISPLAY);
					Object[] disp = connection.handleRequest("get-username", name);
					if(disp == null) return null;
					name = (String) disp[0];
					data = select("highscores", "username=?", GET_HS_DATA, name);
					if(data == null) return null;
				}
				return data == null ? null : new Object[] { (HSData) data[0] };
			case "get-skill":
				int skill_id = (int) data[1];
				data = select("highscores ORDER BY skill_? DESC LIMIT 30", "", GET_SKILL_LIST, skill_id);
				return data == null ? null : new Object[] { (HSDataList) data[0] };
			case "get-list":
				int size = (int) data[1];
				data = select("highscores ORDER BY total_level DESC, total_xp DESC LIMIT ?", "", GET_SKILL_LIST, size);
				return data == null ? null : new Object[] { (HSDataList) data[0] };
		}
		return null;
	}
	
}
