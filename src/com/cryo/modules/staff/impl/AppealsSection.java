package com.cryo.modules.staff.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.PunishmentsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.Appeal;
import com.cryo.modules.account.entities.Punishment;
import com.cryo.modules.staff.entities.StaffSection;
import com.cryo.managers.CookieManager;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

public class AppealsSection implements StaffSection {

	@Override
	public String getName() {
		return "appeals";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Account account = CookieManager.getAccount(request);
		HashMap<String, Object> model = new HashMap<>();
		Properties prop = new Properties();
		Gson gson = Website.getGson();
		switch(action) {
		case "load":
			String html = WebModule.render("./source/modules/staff/sections/appeals/appeals.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "load-list":
			boolean archive = Boolean.parseBoolean(request.queryParams("archive"));
			int page = Integer.parseInt(request.queryParams("page"));
			int type = Integer.parseInt(request.queryParams("type"));
			if (page == 0)
				page = 1;
			Object[] data = PunishmentsConnection.connection().handleRequest("get-appeals", null, archive, page, type);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error loading appeals.");
				break;
			}
			ArrayList<Appeal> appeals = (ArrayList<Appeal>) data[0];
			data = PunishmentsConnection.connection().handleRequest("get-total-appeals-results", null, archive, type);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error loading appeals results total.");
				break;
			}
			int count = (int) data[0];
			model.put("appeals", appeals);
			model.put("archive", archive);
			html = "";
			try {
				html = WebModule.render("./source/modules/staff/sections/appeals/appeals_list.jade", model, request, response);
			} catch(Exception e) {
				e.printStackTrace();
				prop.put("success", false);
				prop.put("error", "Error loading appeal list");
				break;
			}
			prop.put("success", true);
			prop.put("html", html);
			prop.put("pageTotal", count);
			break;
		case "view-appeal":
			int id = Integer.parseInt(request.queryParams("id"));
			data = PunishmentsConnection.connection().handleRequest("get-appeal", id);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error finding punishment.");
				break;
			}
			Appeal appeal = (Appeal) data[0];
			Punishment punishment = appeal.getPunishment();
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
		case "accept-appeal":
		case "decline-appeal":
			id = Integer.parseInt(request.queryParams("id"));
			data = PunishmentsConnection.connection().handleRequest("get-appeal", id);
			appeal = (Appeal) data[0];
			if(account.getRights() < 2 && appeal.getType() == 1) {
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
			PunishmentsConnection.connection().handleRequest("close-appeal", appeal.getId(), action.contains("accept") ? 1 : 2, account.getUsername(), reason);
			if(action.contains("accept"))
				PunishmentsConnection.connection().handleRequest("end-punishment", appeal.getPunishId());
			prop.put("success", true);
			break;
		}
		return gson.toJson(prop);
	}

}
