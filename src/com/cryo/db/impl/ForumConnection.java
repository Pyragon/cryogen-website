package com.cryo.db.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.cryo.db.DatabaseConnection;
import com.cryo.modules.forums.ForumUser;
import com.cryo.modules.index.IndexModule.LatestPost;
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

	@Override
	public Object[] handleRequest(Object... data) {
		String command = ((String) data[0]).toLowerCase();
		switch(command) {
			case "get-user":
				String username = "";
				int uid = -1;
				ResultSet set = null;
				if(data[1] instanceof String) {
					username = (String) data[1];
					set = select("mybb_users", "username='"+username+"'");
				} else {
					uid = (int) data[1];
					set = select("mybb_users", "uid="+uid);
				}
				if(empty(set))
					return null;
				username = getString(set, "username");
				int displaygroup = getInt(set, "displaygroup");
				int usergroup = getInt(set, "usergroup");
				ForumUser user = new ForumUser(uid, username, usergroup, displaygroup);
				return new Object[] { user };
			case "get-latest-threads":
				set = select("mybb_threads", "fid=2 OR fid=4 OR fid=5 AND deletetime=0 ORDER BY dateline DESC LIMIT 5");
				if(set == null || wasNull(set))
					return null;
				PostList list = new PostList();
				while(next(set)) {
					int pid = getInt(set, "firstpost");
					Object[] post = handleRequest("get-post", pid);
					if(post == null)
						continue;
					list.add((LatestPost) post[0]);
				}
				return new Object[] { list };
			case "get-post":
				int pid = (int) data[1];
				set = select("mybb_posts", "pid="+pid);
				if(empty(set))
					return null;
				String subject = getString(set, "subject");
				String message = getString(set, "message");
				message = Utilities.formatMessage(message);
				username = getString(set, "username");
				long dateline = getLongInt(set, "dateline");
				LatestPost post = new LatestPost(subject, message, username, dateline);
				return new Object[] { post };
		}
		return null;
	}
	
}
