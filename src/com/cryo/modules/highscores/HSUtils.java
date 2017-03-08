package com.cryo.modules.highscores;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.HighscoresConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:20:50 PM
 */
public class HSUtils {
	
	public static HSUserList getMiniList(Website website) {
		HighscoresConnection connection = (HighscoresConnection) website.getConnectionManager().getConnection(Connection.HIGHSCORES);
		Object[] data = connection.handleRequest("get-mini-list");
		if(data == null)
			return null;
		return (HSUserList) data[0];
	}
	
}
