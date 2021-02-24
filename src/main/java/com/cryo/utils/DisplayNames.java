package com.cryo.utils;

import com.cryo.entities.accounts.DisplayName;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import static com.cryo.Website.getConnection;

public class DisplayNames {

	private static OrderedBidiMap<String, String> cachedDisplays;
	private static OrderedBidiMap<String, String> cachedLast;

	static {
		cachedDisplays = new TreeBidiMap<>();
		cachedLast = new TreeBidiMap<>();
	}

	public static String getUsername(String displayName) {
		if(cachedDisplays.containsValue(displayName))
			return cachedDisplays.getKey(displayName);
		DisplayName display = getConnection("cryogen_display").selectClass("current_names", "display_name LIKE ?", DisplayName.class, displayName);
		if(display == null)
			return Utilities.formatNameForProtocol(displayName);
		cachedDisplays.put(display.getUsername(), displayName);
		return display.getUsername();
	}

	public static String getDisplayName(String username) {
		if(cachedDisplays.containsKey(username))
			return cachedDisplays.get(username);
		DisplayName display = getConnection("cryogen_display").selectClass("current_names", "username=?", DisplayName.class, username);
		if(display == null)
			return Utilities.formatNameForDisplay(username);
		cachedDisplays.put(username, display.getDisplayName());
		return display.getDisplayName();
	}

	public static boolean nameAllowed(String username) {
		return nameAllowed(username, null);
	}

	public static boolean nameAllowed(String username, String from) {
		DisplayName name = getConnection("cryogen_display").selectClass("current_names", "display_name LIKE ?", DisplayName.class, username);
		if(name != null) return false;
		name = getConnection("cryogen_display").selectClass("last_names", "display_name LIKE ?", DisplayName.class, username);
		if(name != null && !name.getUsername().equals(from)) return false;
		return true;
	}

	public static boolean changeName(String username, String newName) {
		DisplayName name = getConnection("cryogen_display").selectClass("current_names", "username = ?", DisplayName.class, username);
		if(name == null) return false;
		DisplayName last = getConnection("cryogen_display").selectClass("last_names", "username = ?", DisplayName.class, username);
		if(last != null)
			getConnection("cryogen_display").set("last_names", "display_name=?", "username=?", name.getDisplayName(), username);
		else {
			last = new DisplayName(-1, username, name.getDisplayName(), null, null);
			getConnection("cryogen_display").insert("last_names", last.data());
		}
		getConnection("cryogen_display").set("current_names", "display_name=?", "username=?", newName, username);
		cachedDisplays.put(username, newName);
		cachedLast.put(username, name.getDisplayName());
		return true;
	}

}
