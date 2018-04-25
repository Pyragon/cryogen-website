package com.cryo.modules.account.entities;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.comments.Comment;
import com.cryo.db.impl.CommentsConnection;
import com.cryo.modules.account.AccountUtils;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class Report {
	
	protected final int id;
	
	protected final String username, title, info, lastAction;
	
	protected final int commentList;
	
	protected final Timestamp time, archived;
	
	protected final boolean active;
	
	public abstract Object[] getCreationData();
	
	public boolean hasStaffReplied() {
		for(Comment comment : getComments()) {
			Account account = AccountUtils.getAccount(comment.getUsername());
			if(account == null) continue;
			if(account.getRights() > 0)
				return true;
		}
		return false;
	}
	
	public ArrayList<Comment> getComments() {
		Object[] data = CommentsConnection.connection().handleRequest("get-list", commentList);
		if(data == null) return new ArrayList<Comment>();
		return (ArrayList<Comment>) data[0];
	}
	
	public abstract int type();

}
