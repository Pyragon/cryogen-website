package com.cryo.modules.account.entities;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.comments.Comment;

import lombok.Getter;

public class BugReport extends Report {
	
	private @Getter String replicated, seen;

	public BugReport(int id, String username, String title, String replicated, String seen, String info, String lastAction, int commentList, Timestamp time, Timestamp archived, boolean active) {
		super(id, username, title, info, lastAction, commentList, time, archived, active);
		this.replicated = replicated;
		this.seen = seen;
	}
	
	public Object[] getCreationData() {
		return new Object[] { "DEFAULT", username, title, replicated, seen, info, lastAction, commentList, "DEFAULT", "NULL", "DEFAULT", "DEFAULT" };
	}
	
	public int type() {
		return 0;
	}

}
