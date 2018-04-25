package com.cryo.modules.staff;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.comments.Comment;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.account.entities.Report;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 01, 2017 at 4:54:41 AM
 */
@Data
public class BugReport extends Report {
	
	private final int id;
	private final String username, replicated, date, info;
	
	private ArrayList<String> usersRead;
	
	public BugReport(int id, String username, String title, String replicated, String date, String info, String lastAction, String comment, Timestamp time, boolean archived) {
		super(title, lastAction, comment, time, archived);
		this.id = id;
		this.username = username;
		this.replicated = replicated;
		this.date = date;
		this.info = info;
	}
	
	public String[] data() {
		return new String[] { "DEFAULT", username, title, replicated, date, info, "DEFAULT", "", "", "DEFAULT" };
	}
	
	public String type() {
		return "BUG";
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
	
	public ArrayList<Comment> getComments() {
		ArrayList<Comment> comments = new ArrayList<>();
		Object[] data = ReportsConnection.connection().handleRequest("get-comments", id, 1);
		if(data == null)
			return comments;
		return (ArrayList<Comment>) data[0];
	}
}
