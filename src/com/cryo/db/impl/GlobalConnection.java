package com.cryo.db.impl;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.staff.entities.Announcement;
import com.cryo.utils.BCrypt;
import com.cryo.utils.Logger;
import com.cryo.utils.Utilities;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: Mar 7, 2017 at 9:54:51 PM
 */
public class GlobalConnection extends DatabaseConnection {

	public GlobalConnection() {
		super("cryogen_global");
	}

	public static GlobalConnection connection() {
		return (GlobalConnection) Website	.instance()
											.getConnectionManager()
											.getConnection(Connection.GLOBAL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest2(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		try {
			switch (opcode) {
			case "search":
				Properties queryValues = (Properties) data[1];
				int page = (int) data[2];
				boolean archive = (boolean) data[3];
				HashMap<String, String> params = (HashMap<String, String>) data[4];
				String query = (String) queryValues.getProperty("query");
				Object[] values = (Object[]) queryValues.get("values");
				if (page == 0)
					page = 1;
				int offset = (page - 1) * 10;
				query += " AND expiry " + (archive ? "<" : ">") + " NOW()";
				query += " ORDER by date DESC";
				query += " LIMIT " + offset + ",10";
				return select("announcements", query, GET_ANNOUNCEMENTS, values);
			case "search-results":
				queryValues = (Properties) data[1];
				archive = (boolean) data[2];
				params = (HashMap<String, String>) data[3];
				query = (String) queryValues.getProperty("query");
				values = (Object[]) queryValues.get("values");
				query += " AND expiry " + (archive ? "<" : ">") + " NOW()";
				return new Object[] { selectCount("announcements", query, values) };
			case "get-misc-data":
				return select("misc_data", "name=?", GET_MISC_DATA, (String) data[1]);
			case "set-misc-data":
				String name = (String) data[1];
				String value = (String) data[2];
				data = handleRequest2("get-misc-data", name);
				if (data == null)
					insert("misc_data", name, value);
				else
					set("misc_data", "value=?", "name=?", value, name);
				break;
			case "register":
				String username = (String) data[1];
				String password = (String) data[2];
				String salt = BCrypt.generate_salt();
				String hash = BCrypt.hashPassword(password, salt);
				Timestamp date = new Timestamp(Calendar	.getInstance()
														.getTimeInMillis());
				insert("player_data", "DEFAULT", username, hash, salt, 0, 0, null, date);
				DisplayConnection	.connection()
									.handleRequest2("create", username, Utilities.formatName(username));
				return new Object[] { salt, hash };
			case "search-players":
				String text = (String) data[1];
				text = "%" + text + "%";
				data = select("player_data", "username LIKE ? LIMIT 6", SEARCH_ACCOUNTS, text);
				HashMap<String, Account> accounts = new HashMap<>();
				if (data != null) {
					ArrayList<String> list = (ArrayList<String>) data[0];
					for (String user : list) {
						Account account = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, user);
						if (account == null) continue;
						if (!accounts.containsKey(account.getUsername()))
							accounts.put(account.getUsername(), account);
					}
				}
				data = DisplayConnection.connection()
										.handleRequest2("search", "%" + text + "%");
				if (data != null) {
					ArrayList<String> users = (ArrayList<String>) data[0];
					for (String user : users) {
						Account account = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, user);
						if (account == null) continue;
						if (!accounts.containsKey(account.getUsername()))
							accounts.put(account.getUsername(), account);
					}
				}
				return new Object[] { accounts.values() };
			case "compare":
				username = (String) data[1];
				password = (String) data[2];
				data = select("player_data", "username=?", GET_HASH_PASS, username);
				if (data == null)
					return null;
				hash = (String) data[0];
				salt = (String) data[1];
				return new Object[] { BCrypt.hashPassword(password, salt)
											.equals(hash), salt };
			case "change-pass":
				username = (String) data[1];
				password = (String) data[2];
				String current = (String) data[3];
				boolean toCompare = true;
				if (data.length == 5)
					toCompare = (boolean) data[4];
				if (toCompare) {
					data = handleRequest2("compare", username, current);
					if (data == null)
						return new Object[] { false, "Invalid username." };
					boolean compare = (boolean) data[0];
					if (!compare)
						return new Object[] { false, "Invalid current password." };
					salt = (String) data[1];
				} else {
					data = handleRequest2("get-salt", username);
					if (data == null)
						return new Object[] { false, "Invalid username." };
					salt = (String) data[0];
				}
				hash = BCrypt.hashPassword(password, salt);
				set("player_data", "password=?", "username=?", hash, username);
				AccountConnection	.connection()
									.handleRequest2("remove-all-sess", username);
				String sess = (String) AccountConnection.connection()
														.handleRequest2("add-sess", username)[0];
				Website	.instance()
						.getConnectionManager()
						.getConnection(Connection.PREVIOUS)
						.handleRequest2("add-prev-hash", salt, hash);
				return new Object[] { true, sess };
			case "add-prev-all":
				data = select("player_data", null, GET_USERNAMES);
				if (data == null)
					return null;
				ArrayList<String> usernames = (ArrayList<String>) data[0];
				for (String user : usernames) {
					Logger.log(this.getClass(), "Adding previous password for: " + user);
					handleRequest2("add-prev", user);
				}
				break;
			case "get-salt":
				username = (String) data[1];
				data = select("player_data", "username=?", GET_HASH_PASS, username);
				if (data == null)
					return null;
				return new Object[] { (String) data[1] };
			case "add-prev":
				username = (String) data[1];
				data = select("player_data", "username=?", GET_HASH_PASS, username);
				if (data == null)
					return null;
				hash = (String) data[0];
				salt = (String) data[1];
				Website	.instance()
						.getConnectionManager()
						.getConnection(Connection.PREVIOUS)
						.handleRequest2("add-prev-hash", salt, hash);
				break;
			case "get-announcement":
				int id = (int) data[1];
				data = select("announcements", "id=?", GET_ANNOUNCEMENT, id);
				return data == null ? null : new Object[] { (Announcement) data[0] };
			case "get-announcement-count":
				archive = (boolean) data[1];
				String clause = "expiry " + (archive ? "<" : ">") + " NOW()";
				return new Object[] { (int) Utilities.roundUp(selectCount("announcements", clause), 10) };
			case "get-announcements":
				archive = (boolean) data[1];
				page = (int) data[2];
				if (page == 0)
					page = 1;
				offset = (page - 1) * 10;
				data = select("announcements", "expiry " + (archive ? "<" : ">") + " NOW() ORDER BY date DESC LIMIT " + offset + ",10", GET_ANNOUNCEMENTS);
				return data == null ? null : new Object[] { (ArrayList<Announcement>) data[0] };
			case "save-announce":
				Announcement announce = (Announcement) data[1];
				String json = Website	.getGson()
										.toJson(announce.getRead());
				set("announcements", "`read`=?", "id=?", json, announce.getId());
				break;
			case "create-announce":
				announce = (Announcement) data[1];
				insert("announcements", announce.data());
				return new Object[] {};
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private final SQLQuery GET_ANNOUNCEMENT = (set) -> {
		if (empty(set))
			return null;
		return new Object[] { loadAnnouncement(set) };
	};

	private final SQLQuery GET_ANNOUNCEMENTS = (set) -> {
		ArrayList<Announcement> announcements = new ArrayList<>();
		if (wasNull(set))
			return null;
		while (next(set)) {
			announcements.add(loadAnnouncement(set));
		}
		return new Object[] { announcements };
	};

	private Announcement loadAnnouncement(ResultSet set) {
		int id = getInt(set, "id");
		String username = getString(set, "username");
		String title = getString(set, "title");
		String announcement = getString(set, "announcement");
		String read = getString(set, "read");
		ArrayList<String> list = new ArrayList<>();
		if (read != null && !read.equals(""))
			list = Website	.getGson()
							.fromJson(read, ArrayList.class);
		Timestamp date = getTimestamp(set, "date");
		Timestamp expiry = getTimestamp(set, "expiry");
		return new Announcement(id, username, title, announcement, list, date, expiry);
	}

	private final SQLQuery GET_USERNAMES = (set) -> {
		if (wasNull(set))
			return null;
		ArrayList<String> usernames = new ArrayList<>();
		while (next(set)) {
			usernames.add(getString(set, "username"));
		}
		return new Object[] { usernames };
	};

	private final SQLQuery GET_MISC_DATA = (set) -> {
		if (empty(set))
			return null;
		return new Object[] { getString(set, "value") };
	};

	private final SQLQuery GET_HASH_PASS = (set) -> {
		if (empty(set))
			return null;
		String hash = getString(set, "password");
		String salt = getString(set, "salt");
		return new Object[] { hash, salt };
	};

	private final SQLQuery SEARCH_ACCOUNTS = (set) -> {
		ArrayList<String> accounts = new ArrayList<String>();
		if (wasNull(set))
			return null;
		while (next(set)) {
			String username = getString(set, "username");
			accounts.add(username);
		}
		return new Object[] { accounts };
	};

}
