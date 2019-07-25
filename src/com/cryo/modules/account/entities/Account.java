package com.cryo.modules.account.entities;

import com.cryo.Website;
import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.EmailConnection;
import com.cryo.entities.CurrentDisplayName;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.entities.forums.UserGroup;
import com.cryo.modules.highscores.HSUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:55:54 PM
 */
@RequiredArgsConstructor
@Data
public class Account extends MySQLDao {

	@MySQLDefault
	private final int id;
	private final String username;
	private final String password;
	private final String salt;
	private final int rights;
	private final int donator;
	private final String avatarUrl;
	private final int displayGroup;
	@MySQLRead("usergroups")
	private final String usergroupsString;
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

	public String getUserTitle() {
		if (getDisplayGroup() != null && getDisplayGroup().getUserTitle() != null)
			return getDisplayGroup().getUserTitle();
		for (UserGroup group : getUsergroups())
			if (group.getUserTitle() != null) return group.getUserTitle();
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
		ArrayList<Integer> ids = Website.getGson().fromJson(usergroupsString, ArrayList.class);
		if (ids != null) {
			for (int id : ids) {
				Object data = Website.instance().getCachingManager().getData("usergroup-cache", id);
				if (data == null) continue;
				groups.add((UserGroup) data);
			}
		}
		return groups;
	}
	
}
