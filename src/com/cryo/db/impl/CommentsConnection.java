package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.cryo.Website;
import com.cryo.comments.Comment;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;

public class CommentsConnection extends DatabaseConnection {

	public CommentsConnection() {
		super("cryogen_global");
	}
	
	public static CommentsConnection connection() {
		return (CommentsConnection) Website.instance().getConnectionManager().getConnection(Connection.COMMENTS);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		switch(opcode) {
		case "get-list":
			return select("comments", "list_id=?", GET_COMMENT_LIST, (int) data[1]);
		case "get-comment":
			return select("comments", "id=?", GET_COMMENT, (int) data[1]);
		case "add-comment":
			Comment comment = (Comment) data[1];
			insert("comments", comment.getData());
			break;
		case "add-comment-list":
			data = GlobalConnection.connection().handleRequest("get-misc-data", "comment_list_increment");
			int commentList = -1;
			if(data != null)
				commentList = Integer.parseInt((String) data[0]);
			commentList++;
			GlobalConnection.connection().handleRequest("set-misc-data", "comment_list_increment", Integer.toString(commentList));
			return new Object[] { commentList };
		}
		return null;
	}
	
	private final SQLQuery GET_COMMENT = (set) -> {
		if(empty(set)) return null;
		return new Object[] { loadComment(set) };
	};
	
	private final SQLQuery GET_COMMENT_LIST = (set) -> {
		if(wasNull(set)) return null;
		ArrayList<Comment> list = new ArrayList<>();
		while(next(set))
			list.add(loadComment(set));
		return new Object[] { list };
	};
	
	private Comment loadComment(ResultSet set) {
		int id = getInt(set, "id");
		int listId = getInt(set, "list_id");
		String username = getString(set, "username");
		String message = getString(set, "message");
		Timestamp time = getTimestamp(set, "time");
		return new Comment(id, listId, username, message, time);
	}

}
