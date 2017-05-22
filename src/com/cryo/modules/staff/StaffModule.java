package com.cryo.modules.staff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.DatabaseConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountDAO;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.support.punish.AppealDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.modules.account.support.punish.PunishUtils.ReportType;
import com.cryo.modules.staff.appeals.StaffAppealModule;
import com.cryo.modules.staff.bugreports.BugReportsModule;
import com.cryo.modules.staff.playerreports.PlayerReportsModule;
import com.cryo.modules.staff.search.Filter;
import com.cryo.utils.CookieManager;
import com.cryo.utils.DateFormatter;
import com.google.gson.Gson;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
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
	public String decodeRequest(Request request, Response response, RequestType rType) {
		if(!CookieManager.isLoggedIn(request))
			return showLoginPage("/staff", request, response);
		String username = CookieManager.getUsername(request);
		AccountDAO account = AccountUtils.getAccount(username);
		if(account == null)
			return showLoginPage("/staff", request, response);
		if(account.getRights() == 0)
			return Website.render404(request, response);
		HashMap<String, Object> model = new HashMap<>();
		if(rType == RequestType.GET) {
			PunishUtils utils = new PunishUtils();
			int comp_total = 0;
			val appeals = utils.getAppeals(username, false);
			val preports = utils.getPlayerReports(username, false);
			val breports = utils.getBugReports(username, false);
			for(Iterator<?> it = appeals.iterator(); it.hasNext();) {
				it.next();
				if(comp_total++ > 10)
					it.remove();
			}
			for(Iterator<?> it = preports.iterator(); it.hasNext();) {
				it.next();
				if(comp_total++ > 10)
					it.remove();
			}
			for(Iterator<?> it = breports.iterator(); it.hasNext();) {
				it.next();
				if(comp_total++ > 10)
					it.remove();
			}
			int total = appeals.size() + preports.size() + breports.size();
			model.put("total", total);
			model.put("i_appeals", appeals);
			model.put("i_preports", preports);
			model.put("i_breports", breports);
			model.put("appeals", utils.getAppeals(null, false));
			model.put("preports", utils.getPlayerReports(null, false));
			model.put("punishments", utils.getPunishments(null, false));
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
					case "overview":
						switch(action) {
							case "view":
								int id = Integer.parseInt(request.queryParams("id"));
								String type = request.queryParams("type");
								if(type.equals("APPEAL"))
									prop = StaffAppealModule.handleRequest("view-appeal", request, response, prop, this);
								else if(type.equals("PLAYER"))
									prop = PlayerReportsModule.viewReport(id, false, request, response, prop, this);
								else if(type.equals("BUG"))
									prop = BugReportsModule.viewReport(id, false, request, response, prop, this);
								break;
						}
						break;
					case "preport":
						prop = PlayerReportsModule.handleRequest(action, request, response, prop, this);
						break;
					case "breport":
						prop = BugReportsModule.handleRequest(action, request, response, prop, this);
						break;
					case "appeal":
						prop = StaffAppealModule.handleRequest(action, request, response, prop, this);
						break;
					case "punish":
						prop = PunishmentsModule.handleRequest(action, request, response, prop, this);
						break;
				}
				break;
			}
			return new Gson().toJson(prop);
		}
	}
	
}
