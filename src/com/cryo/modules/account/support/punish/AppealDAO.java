package com.cryo.modules.account.support.punish;

import java.sql.Timestamp;
import java.util.ArrayList;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 22, 2017 at 8:41:12 AM
 */
@RequiredArgsConstructor
@Data
public class AppealDAO {
	
	private final int id;
	private final int type;
	private final String username, title, message;
	private String reason;
	private String lastAction;
	private String displayName;
	private final int active;
	private final int punishId;
	private final Timestamp time;
	
	private ArrayList<String> usersRead;
	
	public String getDeclineReason() {
		return reason;
	}
	
	public String type() {
		return "APPEAL";
	}
	
	public boolean isActive() {
		return active == 0;
	}
	
	public Object[] data() {
		return new Object[] { "DEFAULT", type, punishId, username, title, message, "", "DEFAULT", "", 0, "DEFAULT" };
	}
	
	public String getStatus() {
		switch(active) {
			case 0: return "Pending";
			case 1: return "Accepted";
			case 2: return "Declined";
			default: return "Error: contact Admin";
		}
	}
	
	public String getColour() {
		switch(active) {
			case 0: return "";
			case 1: return "color-green";
			case 2: return "color-red";
			default: return "color-red";
		}
	}
	
	public boolean userHasRead(String username) {
		if(usersRead == null)
			return false;
		return usersRead.contains(username);
	}
	
	public void userRead(String username) {
		if(usersRead == null)
			usersRead = new ArrayList<>();
		usersRead.add(username);
	}
	
}
