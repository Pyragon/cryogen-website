package com.cryo.modules.account.entities;

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
public class Appeal {
	
	private final int id;
	private final String username, title, message;
	private String reason;
	private final String lastAction;
	private final int active;
	private final int punishId;
	private final Timestamp time;
	private Timestamp answered;
	
	private ArrayList<String> usersRead;
	
	public String getDeclineReason() {
		return reason;
	}
	
	public boolean isActive() {
		return active == 0;
	}
	
	public Object[] data() {
		return new Object[] { "DEFAULT", punishId, username, title, message, "", "DEFAULT", "", 0, "DEFAULT", "NULL" };
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
			case 0: return "color-yellow";
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
