package com.cryo.modules.account.support.punish;

import java.sql.Timestamp;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 22, 2017 at 2:35:09 AM
 */
@RequiredArgsConstructor
@Data
public class PunishDAO {
	
	private final int id;
	private final String username;
	private final int type;
	private final Timestamp date;
	private final Timestamp expiry;
	private final String punisher;
	private final String reason;
	private final boolean active;
	private final int appealId;
	
	public boolean isAppealable() {
		return appealId == 0;
	}
	
}
