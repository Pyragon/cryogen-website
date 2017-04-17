package com.cryo.modules.account.support;

import java.sql.Timestamp;
import java.util.ArrayList;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 01, 2017 at 4:54:41 AM
 */
@RequiredArgsConstructor
@Data
public class BugReportDAO {
	
	private final int id;
	private final String username, title, replicated, date, info, lastAction, comment;
	private final Timestamp time;
	private Timestamp archived;
	
	private ArrayList<String> usersRead;
	
	public String[] data() {
		return new String[] { "DEFAULT", username, title, replicated, date, info, "DEFAULT", "", "", "DEFAULT" };
	}
	
	public String type() {
		return "BUG";
	}
	
	public boolean isArchived() {
		return archived != null;
	}
	
	public String date() {
		return date;
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
