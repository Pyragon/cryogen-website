package com.cryo.modules.staff.announcements;

import java.sql.Timestamp;
import java.util.ArrayList;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 23, 2017 at 9:02:25 PM
 */
@RequiredArgsConstructor
@Data
public class AnnouncementDAO {
	
	private final int id;
	private final String username;
	private final String title;
	private final String announcement;
	private final ArrayList<String> read;
	private final Timestamp date;
	private final Timestamp expiry;
	
	public boolean hasRead(String username) {
		return read.contains(username);
	}
	
	public String getStatus(String username, boolean numeric) {
		return hasRead(username) ? numeric ? "1" : "Read" : numeric ? "0" : "Unread";
	}
	
	public String getColour(String username) {
		return hasRead(username) ? "color-green" : "color-red";
	}
	
	public Object[] data() {
		return new Object[] { "DEFAULT", username, title, announcement, "", "DEFAULT", expiry };
	}
	
}
