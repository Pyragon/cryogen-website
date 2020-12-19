package com.cryo.utils;

import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.account.entities.Account;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

public class DisplayNames {

	private static OrderedBidiMap<String, String> cachedDisplays;
	private static OrderedBidiMap<String, String> cachedLast;

	public static void init() {
		cachedDisplays = new TreeBidiMap<String, String>();
		cachedLast = new TreeBidiMap<String, String>();
	}

	public static String getUsername(String displayName) {
		if(displayName == null) return null;
		String username = Utilities.formatNameForProtocol(displayName);
		if (cachedDisplays.containsValue(displayName))
			return cachedDisplays.getKey(displayName);
		if (cachedLast.containsValue(displayName))
			return cachedLast.getKey(displayName);
		if(cachedDisplays.containsKey(username))
			return username;
		Account account = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, username);
		if(account != null) return username;
		Object[] data = DisplayConnection.connection().handleRequest("get_username", displayName);
		if (data != null) {
			username = (String) data[0];
			cachedDisplays.put(username, displayName);
			return username;
		}
		data = DisplayConnection.connection().handleRequest("get_username_from_last", displayName);
		if (data != null) {
			username = (String) data[0];
			cachedLast.put(username, displayName);
			return username;
		}
		return null;
	}

	public static String getDisplayName(String username) {
		if (cachedDisplays.containsKey(username))
			return cachedDisplays.get(username);
		Object[] data = DisplayConnection.connection().handleRequest("get_display", username);
		if (data != null) {
			String display = (String) data[0];
			cachedDisplays.put(username, display);
			return display;
		}
		return null;
	}

	public static String getLastDisplayName(String username) {
		if (cachedLast.containsKey(username))
			return cachedLast.get(username);
		Object[] data = DisplayConnection.connection().handleRequest("get_last_display", username);
		if (data != null) {
			String display = (String) data[0];
			cachedLast.put(username, display);
			return display;
		}
		return null;
	}

	public static boolean hasDisplayName(String username) {
		return !getDisplayName(username).equals(Utilities.formatPlayerNameForDisplay(username));
	}

	public static void addDisplayName(String username, String displayName) {
		DisplayConnection.connection().handleRequest("add_display", username, displayName);
		cachedDisplays.put(username, displayName);
	}

	public static void changeDisplayName(String username, String displayName) {
		DisplayConnection.connection().handleRequest("change_display", username,
				getDisplayName(username), displayName);
		String current = getDisplayName(username);
		cachedLast.put(username, current);
		cachedDisplays.put(username, displayName);
	}

}
