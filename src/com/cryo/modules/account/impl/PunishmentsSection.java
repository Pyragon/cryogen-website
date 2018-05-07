package com.cryo.modules.account.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.db.impl.PunishmentsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.AccountSection;
import com.cryo.modules.account.entities.Appeal;
import com.cryo.modules.account.entities.Punishment;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.utils.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

public class PunishmentsSection implements AccountSection {

	@Override
	public String getName() {
		return "punishments";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Properties prop = new Properties();
		Gson gson = new Gson();
		HashMap<String, Object> model = new HashMap<>();
		if(!CookieManager.isLoggedIn(request))
			return gson.toJson(prop);
		Account account = CookieManager.getAccount(request);
		switch(action) {
		case "load":
			try {
				String html = WebModule.render("./source/modules/account/sections/punishments/punishments.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
			} catch(Exception e) {
				e.printStackTrace();
				prop.put("success", false);
				prop.put("error", "Error loading reports section");
			}
			break;
		case "load-list":
			boolean archived = Boolean.parseBoolean(request.queryParams("archived"));
			int page = Integer.parseInt(request.queryParams("page"));
			ArrayList<Punishment> punishments = new PunishUtils().getPunishments(account.getUsername(), archived, page);
			model.put("punishments", punishments);
			String html = WebModule.render("./source/modules/account/sections/punishments/punishments_list.jade", model, request, response);
			Object[] data = PunishmentsConnection.connection().handleRequest("get-total-punish-results", account.getUsername(), archived);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error obtaining full pagetotal");
				break;
			}
			prop.put("success", true);
			prop.put("html", html);
			prop.put("pageTotal", (int) data[0]);
			break;
		case "view-punishment":
			int id = Integer.parseInt(request.queryParams("id"));
			data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Unable to find punishment with that ID.");
				break;
			}
			model.put("punishment", (Punishment) data[0]);
			html = WebModule.render("./source/modules/account/sections/punishments/view_punishment.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "get-appeal-info":
			id = Integer.parseInt(request.queryParams("id"));
			data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Unable to find punishment with that ID.");
				break;
			}
			Punishment punish = (Punishment) data[0];
			if(punish.getAppeal() != null) {
				prop.put("success", false);
				prop.put("error", "Appeal already exists for this punishment!");
				break;
			}
			html = WebModule.render("./source/modules/account/sections/punishments/appeals/create_appeal_info.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "view-create-appeal":
			id = Integer.parseInt(request.queryParams("id"));
			data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Unable to find punishment with that ID.");
				break;
			}
			punish = (Punishment) data[0];
			if(punish.getAppeal() != null) {
				prop.put("success", false);
				prop.put("error", "Appeal already exists for this punishment!");
				break;
			}
			html = WebModule.render("./source/modules/account/sections/punishments/appeals/create_appeal.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "create-appeal":
			id = Integer.parseInt(request.queryParams("id"));
			data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Unable to find punishment with that ID.");
				break;
			}
			punish = (Punishment) data[0];
			if(punish.getAppeal() != null) {
				prop.put("success", false);
				prop.put("error", "Appeal already exists for this punishment!");
				break;
			}
			String title = request.queryParams("title");
			String info = request.queryParams("info");
			if(Utilities.isNullOrEmpty(title, info)) {
				prop.put("success", false);
				prop.put("error", "All fields must be filled out.");
				break;
			}
			if(title.length() > 40) {
				prop.put("success", false);
				prop.put("error", "Title cannot exceed 40 characters.");
				break;
			}
			if(info.length() > 750) {
				prop.put("success", false);
				prop.put("error", "Appeal cannot exceed 750 characters.");
				break;
			}
			try {
				Appeal appeal = new Appeal(0, account.getUsername(), title, info, "Created", 1, id, null);
				data = PunishmentsConnection.connection().handleRequest("create-appeal", appeal);
			} catch(Exception e) {
				prop.put("success", false);
				prop.put("error", "Error occurred while creating appeal. Please try again later.");
				e.printStackTrace();
				break;
			}
			prop.put("success", true);
			break;
		case "view-appeal":
			id = Integer.parseInt(request.queryParams("id"));
			data = PunishmentsConnection.connection().handleRequest("get-punishment", id);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Unable to find punishment with that ID.");
				break;
			}
			punish = (Punishment) data[0];
			if(punish.getAppeal() == null) {
				prop.put("success", false);
				prop.put("error", "No appeal exists for this punishment!");
				break;
			}
			model.put("appeal", punish.getAppeal());
			html = WebModule.render("./source/modules/account/sections/punishments/appeals/view_appeal.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		}
		return gson.toJson(prop);
	}

}
