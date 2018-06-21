package com.cryo.modules.staff.impl;

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
import com.cryo.db.impl.RecoveryConnection;
import com.cryo.db.impl.PreviousConnection.PreviousIP;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.staff.entities.Recovery;
import com.cryo.modules.staff.entities.StaffSection;
import com.cryo.utils.CookieManager;
import com.cryo.utils.DateUtils;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

public class RecoveriesSection implements StaffSection {

	@Override
	public String getName() {
		return "recoveries";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Account account = CookieManager.getAccount(request);
		HashMap<String, Object> model = new HashMap<>();
		Properties prop = new Properties();
		Gson gson = Website.getGson();
		switch(action) {
		case "load":
			String html = WebModule.render("./source/modules/staff/sections/recoveries/recoveries.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "load-list":
			boolean archive = Boolean.parseBoolean(request.queryParams("archive"));
			int page = Integer.parseInt(request.queryParams("page"));
			if(page == 0)
				page = 1;
			Object[] data = RecoveryConnection.connection().handleRequest("get-recoveries", null, archive, page);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error loading recoveries.");
				break;
			}
			ArrayList<Recovery> recoveries = (ArrayList<Recovery>) data[0];
			data = RecoveryConnection.connection().handleRequest("get-total-results", null, archive, page);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error loading recoveries results total.");
				break;
			}
			int count = (int) data[0];
			model.put("recoveries", recoveries);
			model.put("archive", archive);
			html = "";
			try {
				html = WebModule.render("./source/modules/staff/sections/recoveries/recoveries_list.jade", model, request, response);
			} catch(Exception e) {
				e.printStackTrace();
				prop.put("success", false);
				prop.put("error", "Error loading recovery list.");
				break;
			}
			prop.put("success", true);
			prop.put("html", html);
			prop.put("pageTotal", count);
			break;
		case "view-recovery":
			String id = request.queryParams("id");
			data = RecoveryConnection.connection().handleRequest("get-recovery", id);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Cannot find recovery.");
				break;
			}
			Recovery recovery = (Recovery) data[0];
			model.put("recovery", recovery);
			//add results to recovery
			while(true) {
				String enteredEmail = recovery.getEmail();
				data = EmailConnection.connection().handleRequest("get-email", recovery.getUsername());
				model.put("has_email", data != null);
				if(data != null) {
					String realEmail = (String) data[0];
					model.put("real_email", realEmail);
					model.put("correct_email", realEmail.equalsIgnoreCase(enteredEmail));
				}
				int forumId = recovery.getForumId();
				data = ForumConnection.connection().handleRequest("get-uid", recovery.getUsername());
				model.put("has_forum", data != null);
				if(data != null) {
					int realId = (int) data[0];
					model.put("forum_id", realId);
					model.put("correct_forum", realId == forumId);
				}
				// CREATION DATE
				long l = recovery.getCreation();
				Account toRecover = AccountUtils.getAccount(recovery.getUsername());
				if (toRecover == null) {
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
				model.put("cico", recovery.getCico());
				List<PreviousIP> list = PreviousConnection.getSorted(recovery.getUsername());
				ArrayList<String> strings = new ArrayList<>();
				for(int i = 0; i < (list.size() > 5 ? 5 : list.size()); i++)
					strings.add(list.get(i).getIp());
				model.put("list", strings);
				html = "";
				try {
					html = WebModule.render("./source/modules/staff/sections/recoveries/view_recovery.jade", model, request, response);
					prop.put("success", true);
					prop.put("html", html);
					prop.put("active", recovery.getActive() == 0);
				} catch(Exception e) {
					e.printStackTrace();
					prop.put("success", false);
					prop.put("error", "Error loading recovery.");
				}
				break;
			}
			break;
		case "view-response":
			model.put("response", request.queryParams("response"));
			html = WebModule.render("./source/modules/staff/sections/recoveries/view_response.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "accept":
			id = request.queryParams("id");
			data = RecoveryConnection.connection().handleRequest("get-recovery", id);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Cannot find recovery.");
				break;
			}
			recovery = (Recovery) data[0];
			if(recovery.getActive() != 0) {
				prop.put("success", false);
				prop.put("error", "Recovery is not active!");
				break;
			}
			String pass = RandomStringUtils.random(15, true, true);
			RecoveryConnection.connection().handleRequest("set-status", id, 1, pass);
			GlobalConnection.connection().handleRequest("change-pass", recovery.getUsername(), pass, null, false);
			RecoveryConnection.connection().handleRequest("set-email-status", id, 1);
			RecoveryConnection.connection().handleRequest("set-forum-status", id, 1);
			prop.put("success", true);
			break;
		case "decline":
			id = request.queryParams("id");
			String reason = request.queryParams("reason");
			data = RecoveryConnection.connection().handleRequest("get-recovery", id);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Cannot find recovery.");
				break;
			}
			recovery = (Recovery) data[0];
			if(reason.length() < 5 || reason.length() > 75) {
				prop.put("success", false);
				prop.put("error", "You must include a reason of between 5 and 75 characters in order to decline a recovery.");
				break;
			}
			if(recovery.getActive() != 0) {
				prop.put("success", false);
				prop.put("error", "Recovery is not active!");
				break;
			}
			RecoveryConnection.connection().handleRequest("set-status", id, 2, reason);
			RecoveryConnection.connection().handleRequest("set-email-status", id, 1);
			RecoveryConnection.connection().handleRequest("set-forum-status", id, 1);
			prop.put("success", true);
			break;
		}
		return gson.toJson(prop);
	}

}
