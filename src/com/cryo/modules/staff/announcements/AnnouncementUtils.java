package com.cryo.modules.staff.announcements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.WebModule;
import com.cryo.utils.DateSpan;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import lombok.*;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 23, 2017 at 9:02:18 PM
 */
public class AnnouncementUtils {
	
	private static Gson GSON = new Gson();
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Announcement> getAnnouncements(DateSpan span, boolean archive, int page) {
		Object[] data = GlobalConnection.connection().handleRequest("get-announcements", span, archive, page);
		if(data == null)
			return new ArrayList<>();
		return (ArrayList<Announcement>) data[0];
	}
	
	public static String getAnnouncementList(boolean archive, int page, Request request, Response response, WebModule module) {
		HashMap<String, Object> model = new HashMap<>();
		model.put("announcements", getAnnouncements(null, archive, page));
		return module.render("./source/modules/staff/overview/announcements/announcement_list.jade", model, request, response);
	}
	
	public static Announcement getAnnouncement(int id) {
		Object[] data = GlobalConnection.connection().handleRequest("get-announcement", id);
		if(data == null) return null;
		return (Announcement) data[0];
	}
	
	public static void setRead(int id, String username) {
		Announcement announce = getAnnouncement(id);
		if(!announce.getRead().contains(username))
			announce.getRead().add(username);
		GlobalConnection.connection().handleRequest("read-announce", announce);
	}
	
	public static int getTotalPages(boolean archive) {
		Object[] data = GlobalConnection.connection().handleRequest("get-announcement-count", archive);
		if(data == null) return 0;
		int total = (int) data[0];
		total = (int) Utilities.roundUp(total, 10);
		return total;
	}
	
	public static DateSpan getPastWeek() {
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.WEEK_OF_YEAR, -1);
		return new DateSpan(new Date(c.getTimeInMillis()), now);
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> fromString(String string) {
		if(string.equals(""))
			return new ArrayList<>();
		return GSON.fromJson(string, ArrayList.class);
	}
	
	public static String toString(ArrayList<String> list) {
		return GSON.toJson(list);
	}
	
}
