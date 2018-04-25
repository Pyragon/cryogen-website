package com.cryo.modules.staff.recoveries;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

import com.cryo.Website;
import com.cryo.db.impl.EmailConnection;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.PreviousConnection;
import com.cryo.db.impl.PreviousConnection.PreviousIP;
import com.cryo.db.impl.PunishmentConnection;
import com.cryo.db.impl.RecoveryConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.support.punish.AppealDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.utils.CookieManager;
import com.cryo.utils.DateUtils;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

public class RecoveriesModule {

	@SuppressWarnings("unchecked")
	public static Properties handleRequest(String action, Request request, Response response, Properties prop,
			WebModule module) {
		if (CookieManager.getRights(request) < 2) {
			prop.put("success", false);
			prop.put("error", "Insufficient permissions.");
			return prop;
		}
		switch (action) {
		case "search":
			HashMap<String, Object> model = new HashMap<>();
			String text = request.queryParams("search");
			int page = Integer.parseInt(request.queryParams("page"));
			Properties search_results = Website.instance().getSearchManager().search("recover", text, page, RecoveryConnection.connection());
			prop.put("success", search_results.get("success"));
			if(!(boolean) search_results.get("success")) {
				prop.put("error", search_results.get("error"));
				break;
			}
			List<RecoveryDAO> recoveries = (List<RecoveryDAO>) search_results.get("results");
			model.put("recoveries", recoveries);
			String html = module.render("./source/modules/staff/recoveries/recover_list.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			prop.put("pageTotal", search_results.get("pageTotal"));
			prop.put("filters", search_results.get("filters"));
			break;
		case "view-list":
			boolean archived = Boolean.parseBoolean(request.queryParams("archived"));
			page = Integer.parseInt(request.queryParams("page"));
			model = new HashMap<String, Object>();
			recoveries = RecoveryUtils.getRecoveries(archived, page);
			model.put("recoveries", recoveries);
			html = "";
			try {
				html = module.render("./source/modules/staff/recoveries/recover_list.jade", model, request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (html == null || html.equals("")) {
				prop.put("success", false);
				prop.put("error", "Error rendering recovery list.");
				break;
			}
			prop.put("success", true);
			int total = RecoveryUtils.getTotalPages(archived);
			prop.put("pageTotal", total);
			prop.put("html", html);
			break;
		case "view":
			String id = request.queryParams("id");
			Object[] data = RecoveryConnection.connection().handleRequest("get-recovery", id);
			if (data == null) {
				prop.put("success", false);
				prop.put("error", "No recovery found");
				break;
			}
			RecoveryDAO recovery = (RecoveryDAO) data[0];
			model = new HashMap<>();
			model.put("success", true); // we have a recovery.
			model.put("recovery", recovery); // used to display the information
			// GET RESULTS TO ANSWERS
			String username = recovery.getUsername();
			// EMAIL FIRST
			String entered_email = recovery.getEmail();
			data = EmailConnection.connection().handleRequest("get-email", username);
			model.put("has_email", (data != null));
			if (data != null) {
				String real_email = (String) data[0];
				model.put("real_email", real_email);
				model.put("correct_email", real_email.equalsIgnoreCase(entered_email));
			}
			// FORUM ID
			String forum_id = recovery.getForumId();
			data = ForumConnection.connection().handleRequest("get-uid", username);
			model.put("has_forum", (data != null));
			if (data != null) {
				int real_id = (int) data[0];
				model.put("forum_id", Integer.toString(real_id));
				model.put("correct_forum", Integer.toString(real_id).equals(forum_id));
			}
			// CREATION DATE
			long l = recovery.getCreation();
			Account account = AccountUtils.getAccount(username);
			if (account == null) {
				prop.put("success", false);
				prop.put("error", "Error finding account associated.");
				break;
			}
			if (l != 0L) {
				Date date = new Date(l);
				Date created = account.getCreationDate();
				long days_off = DateUtils.getDateDiff(date, created, TimeUnit.DAYS);
				if (days_off < 0)
					days_off = -days_off;
				model.put("days_off", days_off);
			}
			// CITY/COUNTRY
			model.put("cico", recovery.getCico());
			model.put("id", recovery.getId());
			List<PreviousIP> list = PreviousConnection.getSorted(username);
			ArrayList<String> strings = new ArrayList<>();
			if(list == null) {
				for(int i = 0; i < 5; i++) strings.add("");
			} else {
				for(int i = 0; i < 5; i++) {
					if(list.size() >= (i+1))
						strings.add(list.get(i).getIp());
					else
						strings.add("");
				}
			}
			model.put("list", strings);
			html = "";
			try {
				html = module.render("./source/modules/staff/recoveries/view_recovery.jade", model, request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (html == null || html == "") {
				prop.put("success", false);
				prop.put("error", "Error rendering Recovery view template.");
				break;
			}
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "view-noty":
			html = module.render("./source/modules/staff/recoveries/respond_noty.jade", new HashMap<>(), request,
					response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "respond":
			id = request.queryParams("id");
			String answer = request.queryParams("answer");
			String reason = request.queryParams("reason");
			if (id == null || id.equals("")) {
				prop.put("success", false);
				prop.put("error", "Invalid ID.");
				break;
			}
			if (answer == null || answer.equals("")) {
				prop.put("success", false);
				prop.put("error", "Invalid answer.");
				break;
			}
			if (answer.equals("false") && (reason == null || reason.equals(""))) {
				prop.put("success", false);
				prop.put("error", "A reason is required if you are declining a recovery.");
				break;
			}
			if (answer.equals("false") && reason.length() > 75) {
				prop.put("success", false);
				prop.put("error", "Your reason cannot exceed 75 characters.");
				break;
			}
			data = RecoveryConnection.connection().handleRequest("get-recovery", id);
			if (data == null) {
				prop.put("success", false);
				prop.put("error", "Invalid recovery. Please refresh the page.");
				break;
			}
			recovery = (RecoveryDAO) data[0];
			if (recovery.getActive() != 0) {
				prop.put("success", false);
				prop.put("error", "This recovery has already been accepted/denied by other means. Please refresh the page.");
				break;
			}
			boolean accept = answer.equals("true");
			if (accept) { // reset forum/email recoveries. have 'status' on them. 0 = valid (linked to
							// page to reset pass), 1 = invalid (get linked to view page)
				String new_pass = RandomStringUtils.random(15, true, true);
				username = recovery.getUsername();
				try {
					RecoveryConnection.connection().handleRequest("set-status", id, 1, new_pass);
					GlobalConnection.connection().handleRequest("change-pass", username, new_pass, null, false);
				} catch (Exception e) {
					e.printStackTrace();
					prop.put("success", false);
					prop.put("error", "Error occurred in setting statuses. Contact Cody with the ID: " + id + ".");
					break;
				}
			} else {
				// if declining, check if their recovered via forum/email and double check. if
				// true, have option to ban the email/forum recovery method?
				try {
					RecoveryConnection.connection().handleRequest("set-status", id, 2, reason);
				} catch (Exception e) {
					e.printStackTrace();
					prop.put("success", false);
					prop.put("error", "Error occurred in setting statuses. Contact Cody with the ID: " + id + ".");
					break;
				}
			}
			RecoveryConnection.connection().handleRequest("set-email-status", id, 1);
			RecoveryConnection.connection().handleRequest("set-forum-status", id, 1);
			prop.put("success", true);
			break;
		}
		return prop;
	}

}
