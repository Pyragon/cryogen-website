package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.comments.Comment;
import com.cryo.comments.CommentList;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;

public class CommentsConnection extends DatabaseConnection {

	public CommentsConnection() {
		super("cryogen_comments");
	}
	
	public static CommentsConnection connection() {
		return (CommentsConnection) Website.instance().getConnectionManager().getConnection(Connection.COMMENTS);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
		case "get-list":
			int listId = (int) data[1];
			data = select("lists", "list_id=?", GET_COMMENT_LIST, listId);
			if(data == null) return null;
			int rights = (int) data[1];
			String creator = (String) data[2];
			data = select("comments", "list_id=?", GET_COMMENTS, listId);
			if(data == null) return null;
			return new Object[] { new CommentList(listId, rights, creator, (HashMap<Integer, Comment>) data[0]) };
		case "get-comment":
			return select("comments", "id=?", GET_COMMENT, (int) data[1]);
		case "add-comment":
			Comment comment = (Comment) data[1];
			int id = insert("comments", comment.getData());
			return new Object[] { id };
		case "remove-comment":
			id = (int) data[1];
			delete("comments", "id=?", id);
			break;
		case "add-comment-list":
			int rightsReq = 0;
			creator = (String) data[1];
			if(data.length > 2)
				rightsReq = (int) data[2];
			data = GlobalConnection.connection().handleRequest("get-misc-data", "comment_list_increment");
			int commentList = -1;
			if(data != null)
				commentList = Integer.parseInt((String) data[0]);
			commentList++;
			insert("lists", commentList, rightsReq, creator);
			GlobalConnection.connection().handleRequest("set-misc-data", "comment_list_increment", Integer.toString(commentList));
			return new Object[] { commentList };
		}
		return null;
	}
	
	private final SQLQuery GET_COMMENT = (set) -> {
		if(empty(set)) return null;
		return new Object[] { loadComment(set) };
	};
	
	private final SQLQuery GET_COMMENTS = (set) -> {
		if(wasNull(set)) return null;
		HashMap<Integer, Comment> comments = new HashMap<Integer, Comment>();
		while(next(set)) {
			Comment comment = loadComment(set);
			if(comment == null) continue;
			comments.put(comment.getId(), comment);
		}
		return new Object[] { comments };
	};
	
	private final SQLQuery GET_COMMENT_LIST = (set) -> {
		if(empty(set)) return null;
		int listId = getInt(set, "list_id");
		int rights = getInt(set, "rights");
		String creator = getString(set, "creator");
		return new Object[] { listId, rights, creator };
	};
	
	private Comment loadComment(ResultSet set) {
		int id = getInt(set, "id");
		int listId = getInt(set, "list_id");
		String username = getString(set, "username");
		String message = getString(set, "comment");
		Timestamp date = getTimestamp(set, "date");
		return new Comment(id, listId, username, message, date);
	}

}
