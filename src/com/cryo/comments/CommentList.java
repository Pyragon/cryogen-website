package com.cryo.comments;

import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.account.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@AllArgsConstructor
@Data
public class CommentList {
	
	private int listId;
	private int rightsReq;
	private String creator;
	private HashMap<Integer, Comment> comments;
	
	public boolean hasStaffReplied() {
		for(Comment comment : comments.values()) {
			Account account = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, comment.getUsername());
			if(account == null)
				continue;
			if(account.getRights() > 0)
				return true;
		}
		return false;
	}
	
}
