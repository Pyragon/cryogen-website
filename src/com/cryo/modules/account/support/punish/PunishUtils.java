package com.cryo.modules.account.support.punish;

import java.util.ArrayList;

import com.cryo.db.impl.PunishmentConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 22, 2017 at 2:34:49 AM
 */
public class PunishUtils {
	
	@SuppressWarnings("unchecked")
	public ArrayList<PunishDAO> getPunishments(String username) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-punishments", username);
		if(data == null)
			return new ArrayList<PunishDAO>();
		return (ArrayList<PunishDAO>) data[0];
	}
	
}
