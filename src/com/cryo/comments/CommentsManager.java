package com.cryo.comments;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.cryo.db.impl.CommentsConnection;
import com.cryo.modules.WebModule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Synchronized;
import spark.Request;
import spark.Response;

public class CommentsManager {
	
	private HashMap<Integer, CommentList> commentLists;
	
	public CommentsManager() {
		commentLists = new HashMap<>();
	}
	
	public int createCommentList(int rightsReq, String creator) {
		Object[] data = CommentsConnection.connection().handleRequest("add-comment-list", creator, rightsReq);
		if(data == null) return -1;
		int nextListId = (int) data[0];
		CommentList list = new CommentList(nextListId, rightsReq, creator, new HashMap<>());
		commentLists.put(nextListId, list);
		return nextListId;
	}
	
	public CommentList getCommentList(int id) {
		if(commentLists.containsKey(id)) return commentLists.get(id);
		Object[] data = CommentsConnection.connection().handleRequest("get-list", id);
		if(data == null) return null;
		CommentList list = (CommentList) data[0];
		commentLists.put(id, list);
		return list;
	}

	@Synchronized
	public void removeComment(int listId, int id) {
		CommentList list = getCommentList(listId);
		if(list == null) return;
		list.getComments().remove(id);
		commentLists.put(listId, list);
		CommentsConnection.connection().handleRequest("remove-comment", id);
	}
	
	@Synchronized
	public void addComment(String username, String message, int listId) {
		Comment comment = new Comment(-1, listId, username, message, new Timestamp(new Date().getTime()));
		Object[] data = CommentsConnection.connection().handleRequest("add-comment", comment);
		if(data == null) return;
		int id = (int) data[0];
		comment.setId(id);
		CommentList list = getCommentList(listId);
		list.getComments().put(comment.getId(), comment);
		commentLists.put(listId, list);
	}
	
	public String getComments(int listId, Request request, Response response) {
		CommentList list = getCommentList(listId);
		HashMap<String, Object> model = new HashMap<>();
		model.put("list", list);
		String html = WebModule.render("./source/modules/utils/comment_list.jade", model, request, response);
		return html;
	}
	
	
}
