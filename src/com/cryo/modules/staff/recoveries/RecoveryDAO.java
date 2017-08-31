package com.cryo.modules.staff.recoveries;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: July 25, 2017 at 3:38:31 AM
 */
@Data
@RequiredArgsConstructor
public class RecoveryDAO {
	
	private final String id, username, email, forumId;
	
	private final long creation;
	
	private final String cico, additional;
	
	private final int[] passes;
	
	private final int active;
	
	private final String newPass, reason;
	
	private final String ip;
	
	private final Timestamp date;
	
	private ArrayList<String> usersRead;
	
	public Object[] data() {
		return new Object[] { id, username, email, forumId, creation == 0L ? "NULL" : new Timestamp(creation), cico, additional, passes[0], passes[1], passes[2], active, newPass, reason, "", ip, "DEFAULT" };
	}
	
	public int getPass(int index) {
		return passes[index];
	}
	
	public int getDaysOff() {
		if(creation == 0L) return -1;
		return 12;
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
