package com.cryo.utils;

import com.cryo.entities.accounts.CurrentDisplayName;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;

import java.util.HashMap;

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
		CurrentDisplayName display = getConnection("cryogen_display").selectClass("current_names", "display_name=?", CurrentDisplayName.class, displayName);
		if(display == null)
			return Utilities.formatNameForProtocol(displayName);
		return display.getUsername();
	}

	public static String getDisplayName(String username) {
		if(cachedDisplays.containsKey(username))
			return cachedDisplays.get(username);
		CurrentDisplayName display = getConnection("cryogen_display").selectClass("current_names", "username=?", CurrentDisplayName.class, username);
		if(display == null)
			return Utilities.formatNameForDisplay(username);
		return display.getDisplayName();
	}

}
