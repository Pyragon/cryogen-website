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
 * Created on: April 01, 2017 at 4:52:27 AM
 */
@Data
public class PlayerReport extends Report {

	private final int id;
	private final String username, offender, rule, info, proof;
	
	private ArrayList<String> usersRead;
	
	public PlayerReport(int id, String username, String title, String offender, String rule, String info, String proof, String lastAction, String comment, Timestamp time, boolean archived) {
		super(title, lastAction, comment, time, archived);
		this.id = id;
		this.username = username;
		this.offender = offender;
		this.rule = rule;
		this.info = info;
		this.proof = proof;
	}
	
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
	
	public ArrayList<Comment> getComments() {
		ArrayList<Comment> comments = new ArrayList<>();
		Object[] data = ReportsConnection.connection().handleRequest("get-comments", id, 0);
		if(data == null)
			return comments;
		return (ArrayList<Comment>) data[0];
	}
	
}
