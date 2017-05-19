package com.cryo.db.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.modules.forums.ForumUser;
import com.cryo.modules.index.IndexModule.PostDAO;
import com.cryo.modules.index.IndexModule.PostList;
import com.cryo.utils.Utilities;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 7:37:30 PM
 */
public class ForumConnection extends DatabaseConnection {
	
	public ForumConnection() {
		super("cryogen_mybb");
	}
	
	private final SQLQuery GET_USER = (set) -> {
		if(empty(set))
			return null;
		int uid = getInt(set, "uid");
		String username = getString(set, "username");
		int displaygroup = getInt(set, "displaygroup");
		int usergroup = getInt(set, "usergroup");
		ForumUser user = new ForumUser(uid, username, usergroup, displaygroup);
		return new Object[] { user };
	};
	
	private final SQLQuery GET_LATEST_THREADS = (set) -> {
		if(wasNull(set))
			return null;
		PostList list = new PostList();
		while(next(set)) {
			int pid = getInt(set, "firstpost");
			Object[] post = handleRequest("get-post", pid);
			if(post == null)
				continue;
			list.add((PostDAO) post[0]);
		}
		return new Object[] { list };
	};
	
	private final SQLQuery GET_POST = (set) -> {
		if(empty(set))
			return null;
		String subject = getString(set, "subject");
		String message = getString(set, "message");
		message = Utilities.formatMessage(message);
		String username = getString(set, "username");
		long dateline = getLongInt(set, "dateline");
		PostDAO post = new PostDAO(subject, message, username, dateline);
		return new Object[] { post };
	};

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest(Object... data) {
		String command = ((String) data[0]).toLowerCase();
		switch(command) {
			case "get-user":
				String username = "";
				int uid = -1;
				if(data[1] instanceof String) {
					username = (String) data[1];
					data = select("mybb_users", "username=?", GET_USER, username);
				} else {
					uid = (int) data[1];
					data = select("mybb_users", "uid=?", GET_USER, uid);
				}
				return data == null ? null : new Object[] { (ForumUser) data[0] };
			case "get-latest-threads":
				data = select("mybb_threads", "(fid=4 OR fid=5) AND deletetime=0 ORDER BY dateline DESC LIMIT 5", GET_LATEST_THREADS);
				if(data == null) {
					System.out.println("null data");
					return null;
				}
				return data == null ? null : new Object[] { (PostList) data[0] };
			case "get-post":
				int pid = (int) data[1];
				data = select("mybb_posts", "pid=?", GET_POST, pid);
				return data == null ? null : new Object[] { (PostDAO) data[0] };
		}
		return null;
	}
	
}
