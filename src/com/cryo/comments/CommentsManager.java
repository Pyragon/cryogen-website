package com.cryo.comments;

import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.db.impl.CommentsConnection;

public class CommentsManager {
	
	private HashMap<Integer, ArrayList<Comment>> commentLists;
	
	public CommentsManager() {
		commentLists = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Comment> getCommentList(int listId) {
		if(commentLists.containsKey(listId)) return commentLists.get(listId);
		Object[] data = CommentsConnection.connection().handleRequest("get-list", listId);
		if(data == null) return null;
		return (ArrayList<Comment>) data[0];
	}
	
	public int createCommentList() {
		return 0;
	}

	public Comment getComment(int id) {
		Object[] data = CommentsConnection.connection().handleRequest("get-comment", id);
		if(data == null) return null;
		return (Comment) data[0];
	}
	
	public void addComment(String username, String message, int listId) {
		Comment comment = new Comment(-1, listId, username, message, null);
		CommentsConnection.connection().handleRequest("add-comment", comment);
	}
	
}
