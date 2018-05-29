package com.cryo.modules.staff.announcements;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.WebModule;
import com.cryo.utils.DateUtils;
import com.mysql.jdbc.StringUtils;

import lombok.*;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 24, 2017 at 9:51:51 PM
 */
public class AnnouncementModule {
	
	public static Properties handleRequest(String action, String username, Request request, Response response, Properties prop, WebModule module) {
		switch(action) {
			case "view-list":
				boolean archive = Boolean.parseBoolean(request.queryParams("archived"));
				int page = Integer.parseInt(request.queryParams("page"));
				HashMap<String, Object> model = new HashMap<>();
				prop.put("html", AnnouncementUtils.getAnnouncementList(archive, page, request, response, module));
				prop.put("pageTotal", AnnouncementUtils.getTotalPages(archive));
				prop.put("success", true);
				break;
			case "view-announce":
				int id = Integer.parseInt(request.queryParams("id"));
				model = new HashMap<>();
				Announcement announcement = AnnouncementUtils.getAnnouncement(id);
				if(announcement == null) {
					prop.put("success", false);
					prop.put("error", "Invalid announcement id. Please reload the page and try again.");
					break;
				}
				model.put("announcement", AnnouncementUtils.getAnnouncement(id));
				prop.put("success", true);
				prop.put("html", module.render("./source/modules/staff/overview/announcements/view_announcement.jade", model, request, response));
				break;
			case "mark-read":
				id = Integer.parseInt(request.queryParams("id"));
				archive = Boolean.parseBoolean(request.queryParams("archived"));
				page = Integer.parseInt(request.queryParams("page"));
				AnnouncementUtils.setRead(id, username);
				prop.put("success", true);
				prop.put("html", AnnouncementUtils.getAnnouncementList(archive, page, request, response, module));
				prop.put("pageTotal", AnnouncementUtils.getTotalPages(archive));
				break;
			case "view-create-announce":
				model = new HashMap<>();
				String html = module.render("./source/modules/staff/overview/announcements/create_announcement.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "create":
				String title = request.queryParams("title");
				String text = request.queryParams("text");
				String expiry = request.queryParams("expiry");
				StringUtils.isNullOrEmpty(title);
				if(StringUtils.isNullOrEmpty(title) || StringUtils.isNullOrEmpty(text) || StringUtils.isNullOrEmpty(expiry)) {
					prop.put("success", false);
					prop.put("error", "Please enter a valid title, announcement, and expiry date.");
					break;
				}
				if(!DateUtils.isValidDate(expiry, "dd/mm/YYYY")) {
					prop.put("success", false);
					prop.put("error", "Expiry date must be in DD/MM/YYYY format!");
					break;
				}
				Date date = DateUtils.getDate(expiry, "dd/MM/yyyy");
				if(date == null) {
					prop.put("success", false);
					prop.put("error", "Expiry date must be in DD/MM/YYYY format!");
					break;
				}
				int diff = (int) DateUtils.getDateDiff(new Date(), date, TimeUnit.DAYS);
				if(diff < 7) {
					prop.put("success", false);
					prop.put("error", "Announcements must not expire for at least 7 days.");
					break;
				}
				Announcement announce = new Announcement(0, username, title, text, null, null, new Timestamp(date.getTime()));
				Object[] data = GlobalConnection.connection().handleRequest("create-announce", announce);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error inserting announement. Please try again later or contact Cody if this problem persists.");
					break;
				}
				prop.put("success", true);
				prop.put("html", AnnouncementUtils.getAnnouncementList(false, 1, request, response, module));
				break;
		}
		return prop;
	}
	
}
