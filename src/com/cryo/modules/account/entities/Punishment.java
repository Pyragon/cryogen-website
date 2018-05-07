package com.cryo.modules.account.entities;

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
public class Punishment {
	
	private final int id;
	private final String username;
	private final int type;
	private final Timestamp date;
	private final Timestamp expiry;
	private final String punisher;
	private final String reason;
	private final boolean active;
	private final int appealId;
	private final Timestamp archived;
	private final String archiver;
	
	private Appeal appeal;
	
	public boolean isAppealable() {
		return appealId == 0;
	}
	
	public Object[] data() {
		return new Object[] { "DEFAULT", appealId, username, type, "DEFAULT", expiry, punisher, reason, (active ? 1 : 0), archived, archiver };
	}
	
}
