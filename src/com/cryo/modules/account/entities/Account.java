package com.cryo.modules.account.entities;

import com.cryo.Website;
import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.EmailConnection;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.CurrentDisplayName;
import com.cryo.entities.Email;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.entities.forums.AccountStatus;
import com.cryo.entities.forums.UserGroup;
import com.cryo.entities.forums.VisitorMessage;
import com.cryo.modules.highscores.HSUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:55:54 PM
 */
@RequiredArgsConstructor
@Data
@AllArgsConstructor
public class Account extends MySQLDao {

	@MySQLDefault
	private final int id;
    @MySQLRead
	private String username;
    @MySQLRead
	private String password;
    @MySQLRead
	private String salt;
    @MySQLRead
	private int rights;
    @MySQLRead
	private int donator;
    @MySQLRead
	private String avatarUrl;
	@MySQLDefault
    @MySQLRead
	private int displayGroup;
    @MySQLRead
	private String usergroups;
	@MySQLDefault
	private final Timestamp creationDate;

	public HSUtils.HSData getHSData() {
		Object data = Website.instance().getCachingManager().getData("hs-cache", "personal", username);
		if (data == null) return null;
		return (HSUtils.HSData) data;
	}
	
	public String getEmail() {
		Object[] data = EmailConnection.connection().handleRequest("get-email", username);
		if(data == null)
			return "";
		return (String) data[0];
	}

    public void setEmail(String email) {
        if(email.equals("")) {
            EmailConnection.connection().delete("linked", "username=?", username);
            return;
        }
        Email dao = EmailConnection.connection().selectClass("linked", "username=?", Email.class, username);
        if(dao == null) {
            dao = new Email(username, email);
            EmailConnection.connection().insert("linked", dao.data());
        } else
            EmailConnection.connection().set("linked", "email=?", "username=?", email, username);
    }

	public int getThanksGiven() {
		Object data = Website.instance().getCachingManager().getData("thanks-cache", "count-given", id);
		if (data == null) return 0;
		return (int) data;
	}

	public int getThanksReceived() {
		Object data = Website.instance().getCachingManager().getData("thanks-cache", "count-received", id);
		if (data == null) return 0;
		return (int) data;
	}

    public int getPostCount() {
        return ForumConnection.connection().selectCount("posts", "author_id=?", id);
    }

	public String getUserTitle() {
		if (getDisplayGroup() != null && getDisplayGroup().getUserTitle() != null)
			return getDisplayGroup().getUserTitle();
		return null;
	}

	public String getDisplayName() {
		CurrentDisplayName name = DisplayConnection.connection().selectClass("current_names", "username=?", CurrentDisplayName.class, username);
		if (name == null) return username;
		return name.getDisplayName();
	}

	public String getNameColour() {
		if (getDisplayGroup() != null) {
			if (getDisplayGroup().getColour() != null) return getDisplayGroup().getColour();
		}
		for (UserGroup group : getUsergroups()) {
			if (group.getColour() != null) return group.getColour();
		}
		return null;
	}

	public String getImageBefore() {
		if (getDisplayGroup() != null) {
			if (getDisplayGroup().getImageBefore() != null) return getDisplayGroup().getImageBefore();
		}
		for (UserGroup group : getUsergroups()) {
			if (group.getImageBefore() != null) return group.getImageBefore();
		}
		return null;
	}

	public String getImageAfter() {
		if (getDisplayGroup() != null) {
			if (getDisplayGroup().getImageAfter() != null) return getDisplayGroup().getImageAfter();
		}
		for (UserGroup group : getUsergroups()) {
			if (group.getImageAfter() != null) return group.getImageAfter();
		}
		return null;
	}

	public UserGroup getDisplayGroup() {
		Object data = Website.instance().getCachingManager().getData("usergroup-cache", displayGroup);
		if (data == null) return null;
		return (UserGroup) data;
	}

	public ArrayList<UserGroup> getUsergroups() {
		ArrayList<UserGroup> groups = new ArrayList<>();
		ArrayList<Integer> ids = Website.getGson().fromJson(usergroups, ArrayList.class);
		if (ids != null) {
			for (int id : ids) {
				Object data = Website.instance().getCachingManager().getData("usergroup-cache", id);
				if (data == null) continue;
				groups.add((UserGroup) data);
			}
		}
		return groups;
	}

    public String getUsergroupsJoined() {
        return getUsergroups()
                    .stream()
                    .map(UserGroup::getId)
                    .map(i -> Integer.toString(i))
                    .collect(Collectors.joining(","));
    }

    public ArrayList<VisitorMessage> getVisitorMessages() {
		return ForumConnection.connection().selectList("visitor_messages", "account_id=?", "ORDER BY added DESC", VisitorMessage.class, id);
	}

    public void setStatus(int index, int forumId, int userId, int threadId) {
        long millis = System.currentTimeMillis() + (1000 * 60 * 5);
        AccountStatus status = new AccountStatus(-1, id, index, forumId, userId, threadId, new Timestamp(millis));
        ForumConnection.connection().delete("account_statuses", "account_id=?", id);
        ForumConnection.connection().insert("account_statuses", status.data());
    }

    public AccountStatus getStatus() {
        return ForumConnection.connection().selectClass("account_statuses", "account_id=? && expiry > CURRENT_TIMESTAMP()", AccountStatus.class, id);
    }
	
}
