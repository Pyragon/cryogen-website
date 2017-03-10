package com.cryo.modules.highscores;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.HighscoresConnection;
import com.cryo.modules.account.Account;
import com.cryo.modules.account.AccountUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:20:50 PM
 */
public class HSUtils {
	
	public HSData getHSData(String username) {
		Object[] data = getConnection().handleRequest("get-hs-data", username);
		if(data == null)
			return null;
		return (HSData) data[0];
	}
	
	public static HSUserList getMiniList() {
		Object[] data = getConnection().handleRequest("get-mini-list");
		if(data == null)
			return null;
		return (HSUserList) data[0];
	}
	
	public static int[] getRankData(String username) {
		int[] rank = new int[25];
		for(int i = 0; i < rank.length; i++) {
			Object[] data = getConnection().handleRequest("get-rank", username, i);
			if(data == null)
				rank[i] = 0;
			rank[i] = (int) data[0];
		}
		return rank;
	}
	
	public static HighscoresConnection getConnection() {
		return (HighscoresConnection) Website.instance().getConnectionManager().getConnection(Connection.HIGHSCORES);
	}
	
	@RequiredArgsConstructor
	public static class HSData {
		
		private @Getter String name;
		private @Getter String username;
		private final @Getter double totalXP;
		private final @Getter int totalLevel;
		private final @Getter int[] xp;
		private @Getter @Setter int[] rank;

		public int getLevel(int skill) {
			double exp = xp[skill];
			int points = 0;
			int output = 0;
			for (int lvl = 1; lvl <= (skill == 24 ? 120 : 99); lvl++) {
				points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
				output = (int) Math.floor(points / 4);
				if ((output - 1) >= exp) {
					return lvl;
				}
			}
			return skill == 24 ? 120 : 99;
		}
		
		public int getRank(int skill) {
			return rank[skill];
		}
		
		public double getXP(int skill) {
			return xp[skill];
		}
		
		public void formatName(String name) {
			this.username = name;
			this.name = "$for-name="+name+"$end";
		}
	}
	
}
