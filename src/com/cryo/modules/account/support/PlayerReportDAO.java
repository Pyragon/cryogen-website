package com.cryo.modules.account.support;

import java.sql.Timestamp;
import java.util.ArrayList;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 01, 2017 at 4:52:27 AM
 */
@RequiredArgsConstructor
@Data
public class PlayerReportDAO {
	
	private final int id;
	private final String username, title, offender, rule, info, proof, lastAction, comment;
	private final Timestamp time;
	
	private ArrayList<String> usersRead;
	
	public String type() {
		return "PLAYER";
	}
	
	public String[] data() {
		return new String[] { "DEFAULT", username, offender, title, rule, info, proof, "DEFAULT", "", "", "DEFAULT" };
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
