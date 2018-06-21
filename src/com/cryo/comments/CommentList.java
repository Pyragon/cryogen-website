package com.cryo.comments;

import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommentList {
	
	private int listId;
	private int rightsReq;
	private HashMap<Integer, Comment> comments;
	
	public boolean hasStaffReplied() {
		for(Comment comment : comments.values()) {
			Account account = AccountUtils.getAccount(comment.getUsername());
			if(account == null)
				continue;
			if(account.getRights() > 0)
				return true;
		}
		return false;
	}
	
}
