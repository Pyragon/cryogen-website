package com.cryo.modules.account.entities;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.Website;
import com.cryo.comments.Comment;
import com.cryo.comments.CommentList;
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
	
	protected final Timestamp date, archived;
	
	protected final boolean active;
	
	public abstract Object[] getCreationData();
	
	public CommentList getCommentList() {
		return Website.instance().getCommentsManager().getCommentList(commentList);
	}
	
	public abstract int type();

}
