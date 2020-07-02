package com.cryo.modules.account.entities;

import java.sql.Timestamp;

import com.cryo.Website;
import com.cryo.entities.CommentList;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class Report {
	
	protected final int id;
	
	protected final String username, title, info, lastAction;
	
	protected final int commentList;
	
	protected final Timestamp date, archived;
	
	protected String archiver;
	
	protected final boolean active;
	
	public abstract Object[] getCreationData();
	
	public CommentList getCommentList() {
		return Website.instance().getCommentsManager().getCommentList(commentList);
	}
	
	public abstract int type();

}
