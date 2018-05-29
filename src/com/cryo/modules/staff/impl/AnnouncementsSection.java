package com.cryo.modules.staff.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.staff.announcements.Announcement;
import com.cryo.modules.staff.entities.StaffSection;
import com.cryo.utils.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

public class AnnouncementsSection implements StaffSection {

	@Override
	public String getName() {
		return "announcements";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Account account = CookieManager.getAccount(request);
		HashMap<String, Object> model = new HashMap<>();
		Properties prop = new Properties();
		Gson gson = Website.getGson();
		try {
			switch (action) {
			case "load":
				String html = WebModule.render("./source/modules/staff/sections/announcements/announcements.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "load-list":
				boolean archive = Boolean.parseBoolean(request.queryParams("archive"));
				int page = Integer.parseInt(request.queryParams("page"));
				Object[] data = GlobalConnection.connection().handleRequest("get-announcements", archive, page);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error loading announcements.");
					break;
				}
				ArrayList<Announcement> list = (ArrayList<Announcement>) data[0];
				data = GlobalConnection.connection().handleRequest("get-announcement-count", archive);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error loading total results.");
					break;
				}
				int count = (int) data[0];
				model.put("announcements", list);
				model.put("archive", archive);
				html = WebModule.render("./source/modules/staff/sections/announcements/announcement_list.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("pageTotal", count);
				break;
			case "view-create":
				html = WebModule.render("./source/modules/staff/sections/announcements/create_announcement.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "create":
				String title = request.queryParams("title");
				String text = request.queryParams("text");
				String dateText = request.queryParams("expiry");
				if(Utilities.isNullOrEmpty(title, text, dateText)) {
					prop.put("success", false);
					prop.put("error", "All fields must be filled out.");
					break;
				}
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				Timestamp expiry = null;
				try {
					Date date = format.parse(dateText);
					if(date == null) {
						prop.put("success", false);
						prop.put("error", "Expiry date must be in dd/mm/yyyy format.");
						break;
					}
					expiry = new Timestamp(date.getTime());
				} catch(Exception e) {
					prop.put("success", false);
					prop.put("error", "Expiry date must be in dd/mm/yyyy format.");
					break;
				}
				ArrayList<String> readers = new ArrayList<>();
				readers.add(account.getUsername());
				GlobalConnection.connection().handleRequest("create-announce", new Announcement(-1, account.getUsername(), title, text, readers, null, expiry));
				prop.put("success", true);
				break;
			case "read":
				int id = Integer.parseInt(request.queryParams("id"));
				data = GlobalConnection.connection().handleRequest("get-announcement", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "No announcement with that ID.");
					break;
				}
				Announcement announcement = (Announcement) data[0];
				model.put("announcement", announcement);
				html = WebModule.render("./source/modules/staff/sections/announcements/view_announcement.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("unread", !announcement.hasRead(account.getUsername()));
				break;
			case "mark-read":
				id = Integer.parseInt(request.queryParams("id"));
				data = GlobalConnection.connection().handleRequest("get-announcement", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "No announcement with that ID.");
					break;
				}
				announcement = (Announcement) data[0];
				announcement.getRead().add(account.getUsername());
				GlobalConnection.connection().handleRequest("save-announce", announcement);
				prop.put("success", true);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gson.toJson(prop);
	}

}
