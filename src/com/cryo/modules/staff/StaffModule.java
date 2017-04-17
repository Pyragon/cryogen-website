package com.cryo.modules.staff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.cookies.CookieManager;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.Account;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.support.punish.AppealDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.modules.account.support.punish.PunishUtils.ReportType;
import com.cryo.modules.staff.bugreports.BugReportsModule;
import com.cryo.modules.staff.playerreports.PlayerReportsModule;
import com.cryo.utils.DateFormatter;
import com.google.gson.Gson;

import lombok.val;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 30, 2017 at 4:28:29 AM
 */
public class StaffModule extends WebModule {
	
	public static String PATH = "/staff";
	
	public StaffModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		if(!CookieManager.isLoggedIn(request))
			return showLoginPage("/staff", request, response);
		String username = CookieManager.getUsername(request);
		Account account = AccountUtils.getAccount(username);
		if(account == null)
			return showLoginPage("/staff", request, response);
		if(account.getRights() == 0)
			return Website.render404(request, response);
		HashMap<String, Object> model = new HashMap<>();
		if(type == RequestType.GET) {
			PunishUtils utils = new PunishUtils();
			val appeals = utils.getAppeals(username);
			val preports = utils.getPlayerReports(username, false);
			val breports = utils.getBugReports(username);
			int total = appeals.size() + preports.size() + breports.size();
			model.put("total", total);
			model.put("i_appeals", appeals);
			model.put("i_preports", preports);
			model.put("i_breports", breports);
			model.put("appeals", utils.getAppeals(null));
			model.put("preports", utils.getPlayerReports(null, false));
			model.put("breports", utils.getBugReports(null));
			model.put("utils", new PunishUtils());
			model.put("formatter", new DateFormatter());
			return render("./source/modules/staff/index.jade", model, request, response);
		} else {
			Properties prop = new Properties();
			String module = request.queryParams("mod");
			String action = request.queryParams("action");
			while(true) {
				if(module == null || action == null || module.equals("") || action.equals("")) {
					prop.put("success", false);
					prop.put("error", "No module/action provided.");
					break;
				}
				switch(module) {
					case "over":
//						if(action.equals("click-pin")) {
//							//NEED TO SEND BACK REFRESHED IMMEDIATE ACTIONS LIST.
//							int id = Integer.parseInt(request.queryParams("id"));
//							Optional<ReportType> rType = ReportType.getType(request.queryParams("type"));
//							if(!rType.isPresent()) {
//								prop.put("success", false);
//								prop.put("error", "Incorrect report type.");
//								break;
//							}
//							PunishUtils.markReportAsRead(id, username, rType.get());
//							prop.put("success", true);
//						}
						break;
					case "preport":
						prop = PlayerReportsModule.handleRequest(action, request, response, prop, this);
						break;
					case "breport":
						prop = BugReportsModule.handleRequest(action, request, response, prop, this);
						break;
				}
				break;
			}
			return new Gson().toJson(prop);
		}
	}
	
}
