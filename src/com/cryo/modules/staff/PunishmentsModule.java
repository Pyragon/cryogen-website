package com.cryo.modules.staff;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.cryo.Website;
import com.cryo.comments.Comment;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.PunishmentConnection;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.support.punish.PunishDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.modules.search.Filter;
import com.cryo.utils.CookieManager;

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
	public static ArrayList<Comment> getComments(int id) {
		ArrayList<Comment> comments = new ArrayList<>();
		Object[] data = PunishmentConnection.connection().handleRequest("get-comments", id, 1);
		if (data == null)
			return comments;
		return (ArrayList<Comment>) data[0];
	}
	
	public static String[][] FILTERS = { { "type" }, { "username" }, { "expired" }, { "status" } };
	
	public static String[] getFilter(String filter) {
		for(String[] filters : FILTERS) {
			if(filters[0].equalsIgnoreCase(filter))
				return filters;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Properties handleRequest(String action, Request request, Response response, Properties prop, WebModule module) {
		String username = CookieManager.getUsername(request);
		switch (action) {
			case "search":
				HashMap<String, Object> model = new HashMap<>();
				String text = request.queryParams("search");
				int page = Integer.parseInt(request.queryParams("page"));
				Properties search_results = Website.instance().getSearchManager().search("punish", text, page, PunishmentConnection.connection());
				prop.put("success", search_results.get("success"));
				if(!(boolean) search_results.get("success")) {
					prop.put("error", search_results.get("error"));
					break;
				}
				List<PunishDAO> punishments = (List<PunishDAO>) search_results.get("results");
				model.put("punishments", punishments);
				String html = module.render("./source/modules/staff/punishments/punish_list.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("pageTotal", search_results.get("pageTotal"));
				prop.put("filters", search_results.get("filters"));
				break;
			case "view-punish":
				int id = Integer.parseInt(request.queryParams("id"));
				boolean appeal = Boolean.parseBoolean(request.queryParams("appeal"));
				PunishDAO punish = appeal ? new PunishUtils().getPunishmentFromAppeal(id) : new PunishUtils().getPunishment(id);
				if (punish == null) {
					prop.put("success", false);
					prop.put("error", "Invalid punishment ID. Please reload the page and contact an Admin if this persists.");
					break;
				}
				model = new HashMap<>();
				model.put("punish", punish);
				model.put("comments", getComments(id));
				html = module.render("./source/modules/staff/punishments/view_punish.jade", model, request, response);
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
				punish = new PunishDAO(-1, player, (type.equals("mute") ? 0 : 1), null, expiry, username, reason, true, 0);
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
				punish = PunishUtils.getPunishment(id);
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
				Collection<Account> list = (Collection<Account>) data[0];
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
				page = Integer.parseInt(request.queryParams("page"));
				model = new HashMap<String, Object>();
				punishments = new PunishUtils().getPunishments(null, archived, page);
				model.put("punishments", punishments);
				html = module.render("./source/modules/staff/punishments/punish_list.jade", model, request, response);
				prop.put("success", true);
				int total = PunishUtils.getTotalPages(PunishmentConnection.connection(), "punishments"+(archived ? "-a":""));
				prop.put("pageTotal", total);
				prop.put("html", html);
				break;
		}
		return prop;
	}
	
}
