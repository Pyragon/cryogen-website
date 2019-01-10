package com.cryo.modules.staff.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;

import com.cryo.Website;
import com.cryo.db.impl.CommentsConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.PunishmentsConnection;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.Appeal;
import com.cryo.modules.account.entities.Punishment;
import com.cryo.modules.staff.entities.StaffSection;
import com.cryo.utils.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;

import spark.Request;
import spark.Response;

public class PunishmentsSection implements StaffSection {

	@Override
	public String getName() {
		return "punishments";
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
				String html = WebModule.render("./source/modules/staff/sections/punishments/punishments.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "load-list":
				boolean archive = Boolean.parseBoolean(request.queryParams("archive"));
				int page = Integer.parseInt(request.queryParams("page"));
				int type = Integer.parseInt(request.queryParams("type"));
				if (page == 0)
					page = 1;
				Object[] data = PunishmentsConnection	.connection()
														.handleRequest("get-punishments", null, archive, page, type);
				if (data == null) {
					prop.put("success", false);
					prop.put("error", "Error loading punishments.");
					break;
				}
				ArrayList<Punishment> punishments = (ArrayList<Punishment>) data[0];
				data = PunishmentsConnection.connection()
											.handleRequest("get-total-punishments-results", null, archive, page, type);
				if (data == null) {
					prop.put("success", false);
					prop.put("error", "Error loading punishment results total.");
					break;
				}
				int count = (int) data[0];
				model.put("punishments", punishments);
				html = WebModule.render("./source/modules/staff/sections/punishments/punishments_list.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("pageTotal", count);
				break;
			case "view-create":
				html = WebModule.render("./source/modules/staff/sections/punishments/create_punishment.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "search-players":
				String name = request.queryParams("text");
				ArrayList<Properties> users = new ArrayList<>();
				if (Utilities.isNullOrEmpty(name)) {
					prop.put("success", true);
					prop.put("users", gson.toJson(users));
					break;
				}
				data = GlobalConnection	.connection()
										.handleRequest("search-players", name);
				if (data == null) {
					prop.put("success", true);
					prop.put("users", gson.toJson(users));
					break;
				}
				Collection<Account> names = (Collection<Account>) data[0];
				for (Account acc : names) {
					Properties user = new Properties();
					user.put("username", acc.getUsername());
					user.put("display", AccountUtils.crownHTML(acc));
					users.add(user);
				}
				prop.put("success", true);
				prop.put("list", gson.toJson(users));
				break;
			case "view-extend":
				html = WebModule.render("./source/modules/staff/sections/punishments/extend_punishment.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "view-punishment":
				int id = Integer.parseInt(request.queryParams("id"));
				data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Unable to find punishment with that ID.");
					break;
				}
				Punishment punishment = (Punishment) data[0];
				model.put("punishment", punishment);
				model.put("list", punishment.getCommentList());
				model.put("staff", true);
				html = WebModule.render("./source/modules/account/sections/punishments/view_punishment.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("status", punishment.isActive() ? 1 : 0);
				break;
			case "view-appeal":
				id = Integer.parseInt(request.queryParams("id"));
				data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error finding punishment.");
					break;
				}
				punishment = (Punishment) data[0];
				if(punishment.getAppeal() == null) {
					prop.put("success", false);
					prop.put("error", "No appeal found for this punishment!");
					break;
				}
				Appeal appeal = punishment.getAppeal();
				model.put("appeal", appeal);
				model.put("list", appeal.getCommentList());
				html = WebModule.render("./source/modules/account/sections/punishments/appeals/view_appeal.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				int status = appeal.getActive();
				if(!punishment.isActive()) status = 1;
				prop.put("status", status);
				prop.put("type", punishment.getType());
				break;
			case "end-punishment":
				if(account.getRights() < 2) {
					prop.put("success", false);
					prop.put("error", "Only Admins can end punishments.");
					break;
				}
				id = Integer.parseInt(request.queryParams("id"));
				data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error finding punishment.");
					break;
				}
				punishment = (Punishment) data[0];
				PunishmentsConnection.connection().handleRequest("end-punishment", id);
				if(punishment.getAppeal() != null) {
					prop.put("appeal", true);
					PunishmentsConnection.connection().handleRequest("close-appeal", punishment.getAppealId(), 1, account.getUsername());
				}
				prop.put("success", true);
				break;
			case "accept-appeal":
			case "decline-appeal":
				id = Integer.parseInt(request.queryParams("id"));
				data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error finding punishment.");
					break;
				}
				punishment = (Punishment) data[0];
				if(punishment.getAppeal() == null) {
					prop.put("success", false);
					prop.put("error", "No appeal found for this punishment!");
					break;
				}
				if(account.getRights() < 2 && punishment.getType() == 1) {
					prop.put("success", false);
					prop.put("error", "Only Admins can accept or decline ban appeals.");
					break;
				}
				String reason = "";
				if(action.contains("decline")) {
					reason = request.queryParams("reason");
					if(reason == null || reason.length() < 5) {
						prop.put("success", false);
						prop.put("error", "You must provide a reason of at least 5 characters to decline an appeal.");
						break;
					}
				}
				appeal = punishment.getAppeal();
				PunishmentsConnection.connection().handleRequest("close-appeal", punishment.getAppealId(), action.contains("accept") ? 1 : 2, account.getUsername(), reason);
				if(action.contains("accept"))
					PunishmentsConnection.connection().handleRequest("end-punishment", id);
				prop.put("success", true);
				break;
			case "view-accept-appeal":
			case "view-decline-appeal":
				model.put("decline", action.contains("decline"));
				html = WebModule.render("./source/modules/staff/sections/punishments/respond_appeal.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "change-punishment-expiration":
				if(account.getRights() < 2) {
					prop.put("success", false);
					prop.put("error", "Only Admins can edit punishments.");
					break;
				}
				id = Integer.parseInt(request.queryParams("id"));
				data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error finding punishment.");
					break;
				}
				punishment = (Punishment) data[0];
				String expiryString = request.queryParams("expiry");
				boolean permanent = expiryString.toLowerCase().equals("never");
				if(permanent && punishment.getExpiry() == null) {
					prop.put("success", false);
					prop.put("error", "This punishment has already been made permanent!");
					break;
				}
				Timestamp expiry = null;
				if(!permanent) {
					try {
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
						Date date = format.parse(expiryString);
						expiry = new Timestamp(date.getTime());
					} catch(Exception e) {
						prop.put("success", false);
						prop.put("error", "Error parsing expiration date. Please use dd/MM/yyyy and try again.");
						break;
					}
				}
				PunishmentsConnection.connection().handleRequest("change-punishment-expiration", id, expiry);
				prop.put("success", true);
				break;
			case "create":
				name = request.queryParams("name");
				String createType = request.queryParams("type");
				String severity = request.queryParams("severity");
				String createLength = request.queryParams("length");
				reason = request.queryParams("reason");
				if (Utilities.isNullOrEmpty(name)) {
					prop.put("success", false);
					prop.put("error", "Please provide a name!");
					break;
				}
				if(reason.length() < 15) {
					prop.put("success", false);
					prop.put("error", "Please fill out a reason with at least 15 characters!");
					break;
				}
				if (!ArrayUtils.contains(new String[] { "mute", "ban" }, createType)) {
					prop.put("success", false);
					prop.put("error", "Invalid type. Please select again.");
					break;
				}
				if (!ArrayUtils.contains(new String[] { "ip", "account" }, severity)) {
					prop.put("success", false);
					prop.put("error", "Invalid severity. Please select again.");
					break;
				}
				if (!ArrayUtils.contains(new String[] { "1", "2", "7", "-1" }, createLength)) {
					prop.put("success", false);
					prop.put("error", "Invalid length. Please select again.");
					break;
				}
				if(account.getRights() == 1) {
					if(createType.equals("ban")) {
						prop.put("success", false);
						prop.put("error", "Only Admins can ban players!");
						break;
					}
					if(severity.equals("ip")) {
						prop.put("success", false);
						prop.put("error", "Only Admins can mute IPs!");
						break;
					}
					if(createLength.equals("-1")) {
						prop.put("success", false);
						prop.put("error", "Only Admins can permanently mute players!");
						break;
					}
				}
				if(AccountUtils.getAccount(name) == null) {
					prop.put("success", false);
					prop.put("error", "Unable to find a player with that username! Please try again and select from the list.");
					break;
				}
				int length = Integer.parseInt(createLength);
				type = createType.equals("mute") ? 0 : 1;
				expiry = null;
				if(!severity.equals("-1")) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DAY_OF_YEAR, length);
					expiry = new Timestamp(c.getTimeInMillis());
				}
				int commentList = Website.instance().getCommentsManager().createCommentList(1, account.getUsername());
				punishment = new Punishment(-1, name, type, null, expiry, account.getUsername(), reason, true, -1, null, null, commentList);
				PunishmentsConnection.connection().handleRequest("create-punishment", punishment);
				prop.put("success", true);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			prop.put("success", false);
			prop.put("error", "Error in punishments section.");
			return gson.toJson(prop);
		}
		return gson.toJson(prop);
	}

}
