package com.cryo.db.impl;

import java.sql.ResultSet;

import com.cryo.db.DatabaseConnection;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.highscores.HSUser;
import com.cryo.modules.highscores.HSUserList;
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
			case "get-mini-list":
				ResultSet set = selectAll("highscores", "ORDER BY total_level DESC, total_xp DESC LIMIT 10");
				if(set == null || wasNull(set))
					return null;
				HSUserList list = new HSUserList();
				int index = 1;
				while(next(set)) {
					int rank = index++;
					String name = getString(set, "username");
					String total_level = Utilities.formatNumer(getInt(set, "total_level"));
					String total_xp = Utilities.formatNumer(getInt(set, "total_xp"));
					name = "$for-name="+name+"$end";
					list.add(new HSUser(rank, name, total_level, total_xp));
				}
				return new Object[] { list };
		}
		return null;
	}
	
}
