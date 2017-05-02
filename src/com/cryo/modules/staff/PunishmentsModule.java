package com.cryo.modules.staff;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.cryo.cookies.CookieManager;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.PunishmentConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.Account;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.support.punish.PunishDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.utils.CommentDAO;

import lombok.*;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: April 19, 2017 at 11:50:41 PM
 */
public class PunishmentsModule {
	
	@SuppressWarnings("unchecked")
	public static ArrayList<CommentDAO> getComments(int id) {
		ArrayList<CommentDAO> comments = new ArrayList<>();
		Object[] data = PunishmentConnection.connection().handleRequest("get-comments", id, 1);
		if (data == null)
			return comments;
		return (ArrayList<CommentDAO>) data[0];
	}
	
	@SuppressWarnings("unchecked")
	public static Properties handleRequest(String action, Request request, Response response, Properties prop, WebModule module) {
		String username = CookieManager.getUsername(request);
		switch (action) {
			case "view-punish":
				int id = Integer.parseInt(request.queryParams("id"));
				boolean appeal = request.queryParams("appeal") != null;
				PunishDAO punish = appeal ? new PunishUtils().getPunishmentFromAppeal(id) : new PunishUtils().getPunishment(id);
				if (punish == null) {
					prop.put("success", false);
					prop.put("error", "Invalid punishment ID. Please reload the page and contact an Admin if this persists.");
					break;
				}
				HashMap<String, Object> model = new HashMap<>();
				model.put("punish", punish);
				model.put("comments", getComments(id));
				String html = module.render("./source/modules/staff/punishments/view_punish.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "create":
				String player = request.queryParams("player");
				String type = request.queryParams("type");
				int expires = Integer.parseInt(request.queryParams("expiry"));
				String severity = request.queryParams("severity");
				String reason = request.queryParams("reason");
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, expires);
				Timestamp expiry = new Timestamp(cal.getTimeInMillis());
				punish = new PunishDAO(-1, player, (type == "mute" ? 0 : 1), null, expiry, username, reason, true, 0);
				Object[] data = PunishmentConnection.connection().handleRequest("create-punishment", punish);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error adding punishment!");
					break;
				}
				prop.put("success", true);
				model = new HashMap<String, Object>();
				model.put("punishments", new PunishUtils().getPunishments(null, false));
				html = module.render("./source/modules/staff/punishments/punish_list.jade", model, request, response);
				prop.put("html", html);
				break;
			case "view-extend":
				html = module.render("./source/modules/staff/punishments/extend_noty.jade", new HashMap<String, Object>(), request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "extend":
				id = Integer.parseInt(request.queryParams("id"));
				expires = Integer.parseInt(request.queryParams("expiry"));
				punish = new PunishUtils().getPunishment(id);
				if(punish.getExpiry() == null) {
					prop.put("success", false);
					prop.put("error", "This punishment is already permanent. It cannot be extended further.");
					break;
				}
				if(expires != 0) {
					cal = Calendar.getInstance();
					cal.setTimeInMillis(punish.getExpiry().getTime());
					cal.add(Calendar.DAY_OF_MONTH, expires);
					expiry = new Timestamp(cal.getTimeInMillis());
				} else
					expiry = null;
				PunishmentConnection.connection().handleRequest("extend-punishment", expiry, id);
				PunishmentConnection.connection().handleRequest("add-comment", "cryobot", id, 1, "$for-name="+username+"$end has extended this punishment "+(expires == 0 ? "permanently" : "by "+expires+" days")+".");
				Properties proper = new Properties();
				proper = handleRequest("view-punish", request, response, proper, module);
				boolean success = (boolean) proper.get("success");
				if(!success) {
					prop.put("success", false);
					prop.put("error", proper.get("error"));
					break;
				}
				prop.put("success", true);
				prop.put("html", proper.get("html"));
				break;
			case "view_create":
				html = module.render("./source/modules/staff/punishments/create_punish.jade", new HashMap<String, Object>(), request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "search-p":
				String search = request.queryParams("text");
				if (search.equals("") || search.replaceAll(" ", "").equals("")) {
					prop.put("success", false);
					break;
				}
				data = GlobalConnection.connection().handleRequest("search", search);
				if (data == null) {
					prop.put("success", false);
					break;
				}
				ArrayList<Account> list = (ArrayList<Account>) data[0];
				Object[] array = new Object[list.size() * 2];
				int index = 0;
				for(Account account : list) {
					array[index++] = account.getUsername();
					array[index++] = AccountUtils.crownHTML(account);
				}
				prop.put("success", true);
				prop.put("list", array);
				break;
			case "view-list":
				boolean archived = Boolean.parseBoolean(request.queryParams("archived"));
				model = new HashMap<String, Object>();
				ArrayList<PunishDAO> punishments = new PunishUtils().getPunishments(null, archived);
				model.put("punishments", punishments);
				html = module.render("./source/modules/staff/punishments/punish_list.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
		}
		return prop;
	}
	
}
