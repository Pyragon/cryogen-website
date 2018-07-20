package com.cryo.db.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.cryo.Website;
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
public class MyBBConnection extends DatabaseConnection {
	
	public MyBBConnection() {
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
		int pid = getInt(set, "pid");
		String subject = getString(set, "subject");
		String message = getFormattedPost(pid);
		message = Utilities.formatMessage(message);
		String username = getString(set, "username");
		long dateline = getLongInt(set, "dateline");
		PostDAO post = new PostDAO(subject, message, username, dateline);
		return new Object[] { post };
	};
	
	private static String getFormattedPost(int pid) {
		String path = Website.getProperties().getProperty("forum-path");
		String url = "http://"+path+"/parse_message.php?pid="+pid;
		String[] website = Utilities.getWebsite(url);
		if(website == null)
			return url;
		StringBuilder builder = new StringBuilder();
		for(String s : website)
			builder.append(s);
		return builder.toString();
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String command = ((String) data[0]).toLowerCase();
		switch(command) {
			case "get-user":
				int uid;
				if(data[1] instanceof String) {
					String username = (String) data[1];
					data = select("mybb_users", "username=?", GET_USER, username);
				} else {
					uid = (int) data[1];
					data = select("mybb_users", "uid=?", GET_USER, uid);
				}
				return data;
			case "get-latest-threads":
				return select("mybb_threads", "(fid=4 OR fid=5) AND deletetime=0 ORDER BY dateline DESC LIMIT 5", GET_LATEST_THREADS);
			case "get-post":
				return select("mybb_posts", "pid=?", GET_POST, (int) data[1]);
		}
		return null;
	}
	
}
