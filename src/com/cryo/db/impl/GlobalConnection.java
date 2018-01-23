package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import com.cryo.Website;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.modules.account.AccountDAO;
import com.cryo.modules.staff.announcements.AnnouncementDAO;
import com.cryo.modules.staff.announcements.AnnouncementUtils;
import com.cryo.utils.BCrypt;
import com.cryo.utils.CookieManager;
import com.cryo.utils.DateSpan;
import com.cryo.utils.Logger;
import com.cryo.utils.Utilities;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:54:51 PM
 */
public class GlobalConnection extends DatabaseConnection {
	
	public GlobalConnection() {
		super("cryogen_global");
	}
	
	public static GlobalConnection connection() {
		return (GlobalConnection) Website.instance().getConnectionManager().getConnection(Connection.GLOBAL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = ((String) data[0]).toLowerCase();
		try {
			switch(opcode) {
				case "get-misc-data":
					String name = (String) data[1];
					data = select("misc_data", "name=?", GET_MISC_DATA, name);
					if(data == null) return null;
					return new Object[] { data[0] };
				case "register":
					String username = (String) data[1];
					String password = (String) data[2];
					String salt = BCrypt.generate_salt();
					String hash = BCrypt.hashPassword(password, salt);
					Timestamp date = new Timestamp(Calendar.getInstance().getTimeInMillis());
					insert("player_data", "DEFAULT", username, hash, salt, 0, 0, date);
					DisplayConnection.connection().handleRequest("create", username, Utilities.formatName(username));
					return new Object[] { true };
				case "search":
					String text = (String) data[1];
					text = "%"+text+"%";
					data = select("player_data", "username LIKE ?", SEARCH_ACCOUNTS, text);
					if(data == null) return null;
					ArrayList<String> list = (ArrayList<String>) data[0];
					ArrayList<AccountDAO> accounts = new ArrayList<>();
					for(String user : list) {
						data = handleRequest("get-account", user);
						if(data == null) continue;
						accounts.add((AccountDAO) data[0]);
					}
					return new Object[] { accounts };
				case "compare":
					username = (String) data[1];
					password = (String) data[2];
					data = select("player_data", "username=?", GET_HASH_PASS, username);
					if(data == null)
						return null;
					hash = (String) data[0];
					salt = (String) data[1];
					return new Object[] { BCrypt.hashPassword(password, salt).equals(hash), salt };
				case "change-pass":
					username = (String) data[1];
					password = (String) data[2];
					String current = (String) data[3];
					boolean toCompare = true;
					if(data.length == 5)
						toCompare = (boolean) data[4];
					if(toCompare) {
						data = handleRequest("compare", username, current);
						if(data == null)
							return new Object[] { false, "Invalid username." };
						boolean compare = (boolean) data[0];
						if(!compare)
							return new Object[] { false, "Invalid current password." };
						salt = (String) data[1];
					} else {
						data = handleRequest("get-salt", username);
						if(data == null)
							return new Object[] { false, "Invalid username." };
						salt = (String) data[0];
					}
					hash = BCrypt.hashPassword(password, salt);
					set("player_data", "password=?", "username=?", hash, username);
					AccountConnection.connection().handleRequest("remove-all-sess", username);
					String sess = (String) AccountConnection.connection().handleRequest("add-sess", username)[0];
					Website.instance().getConnectionManager().getConnection(Connection.PREVIOUS).handleRequest("add-prev-hash", salt, hash);
					return new Object[] { true, sess };
				case "add-prev-all":
					data = select("player_data", null, GET_USERNAMES);
					if(data == null) return null;
					ArrayList<String> usernames = (ArrayList<String>) data[0];
					for(String user : usernames) {
						Logger.log(this.getClass(), "Adding previous password for: "+user);
						handleRequest("add-prev", user);
					}
					break;
				case "get-salt":
					username = (String) data[1];
					data = select("player_data", "username=?", GET_HASH_PASS, username);
					if(data == null) return null;
					return new Object[] { (String) data[1] };
				case "add-prev":
					username = (String) data[1];
					data = select("player_data", "username=?", GET_HASH_PASS, username);
					if(data == null) return null;
					hash = (String) data[0];
					salt = (String) data[1];
					Website.instance().getConnectionManager().getConnection(Connection.PREVIOUS).handleRequest("add-prev-hash", salt, hash);
					break;
				case "get-account":
					username = (String) data[1];
					data = select("player_data", "username=?", GET_ACCOUNT, username);
					return data == null ? null : new Object[] { ((AccountDAO) data[0]) };
				case "get-announcement":
					int id = (int) data[1];
					data = select("announcements", "id=?", GET_ANNOUNCEMENT, id);
					return data == null ? null : new Object[] { (AnnouncementDAO) data[0] };
				case "get-announcement-count":
					boolean archive = (boolean) data[1];
					String clause = "expiry "+(archive ? "<" : ">")+" NOW()";
					return new Object[] { selectCount("announcements", clause) };
				case "get-announcements":
					DateSpan span = (DateSpan) data[1];
					archive = (boolean) data[2];
					int page = (int) data[3];
					if(page == 0) page = 1;
					int offset = (page - 1) * 10;
					clause = "";
					if(span != null)
						clause = "date >= "+span.format("from")+" AND date <= "+span.format("to");
					else {
						clause = "expiry "+(archive ? "<" : ">")+" NOW()";
					}
					clause += " ORDER BY date DESC LIMIT "+offset+",10";
					data = select("announcements", clause, GET_ANNOUNCEMENTS);
					return data == null ? null : new Object[] { (ArrayList<AnnouncementDAO>) data[0] };
				case "read-announce":
					AnnouncementDAO announce = (AnnouncementDAO) data[1];
					String json = AnnouncementUtils.toString(announce.getRead());
					set("announcements", "`read`=?", "id=?", json, announce.getId());
					break;
				case "create-announce":
					announce = (AnnouncementDAO) data[1];
					insert("announcements", announce.data());
					return new Object[] { };
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private final SQLQuery GET_ANNOUNCEMENT = (set) -> {
		if(empty(set)) return null;
		return new Object[] { loadAnnouncement(set) };
	};
	
	private final SQLQuery GET_ANNOUNCEMENTS = (set) -> {
		ArrayList<AnnouncementDAO> announcements = new ArrayList<>();
		if(wasNull(set)) return null;
		while(next(set)) {
			announcements.add(loadAnnouncement(set));
		}
		return new Object[] { announcements };
	};
	
	private AnnouncementDAO loadAnnouncement(ResultSet set) {
		int id = getInt(set, "id");
		String username = getString(set, "username");
		String title = getString(set, "title");
		String announcement = getString(set, "announcement");
		String read = getString(set, "read");
		Timestamp date = getTimestamp(set, "date");
		Timestamp expiry = getTimestamp(set, "expiry");
		return new AnnouncementDAO(id, username, title, announcement, AnnouncementUtils.fromString(read), date, expiry);
	}
	
	private final SQLQuery GET_USERNAMES = (set) -> {
		if(wasNull(set)) return null;
		ArrayList<String> usernames = new ArrayList<>();
		while(next(set)) {
			usernames.add(getString(set, "username"));
		}
		return new Object[] { usernames };
	};
	
	private final SQLQuery GET_MISC_DATA = (set) -> {
		if(empty(set))
			return null;
		return new Object[] { getString(set, "value") };
	};
	
	private final SQLQuery GET_HASH_PASS = (set) -> {
		if(empty(set))
			return null;
		String hash = getString(set, "password");
		String salt = getString(set, "salt");
		return new Object[] { hash, salt };
	};
	
	private final SQLQuery GET_ACCOUNT = (set) -> {
		if(empty(set))
			return null;
		String username = getString(set, "username");
		int id = getInt(set, "id");
		int rights = getInt(set, "rights");
		int donator = getInt(set, "donator");
		Timestamp date = getTimestamp(set, "creation_date");
		AccountDAO account = new AccountDAO(username, id, rights, donator, date);
		return new Object[] { account };
	};
	
	private final SQLQuery SEARCH_ACCOUNTS = (set) -> {
		ArrayList<String> accounts = new ArrayList<String>();
		if(wasNull(set))
			return null;
		while(next(set)) {
			String username = getString(set, "username");
			accounts.add(username);
		}
		return new Object[] { accounts };
	};
	
}
