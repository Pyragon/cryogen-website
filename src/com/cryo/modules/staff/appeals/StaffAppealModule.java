package com.cryo.modules.staff.appeals;

import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.cookies.CookieManager;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.modules.account.support.punish.PunishUtils.ReportType;

import lombok.*;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 18, 2017 at 2:09:07 AM
 */
public class StaffAppealModule {
	
	public static Properties handleRequest(String action, Request request, Response response, Properties prop, WebModule module) {
		String username = CookieManager.getUsername(request);
		switch(action) {
			case "view-list":
				HashMap<String, Object> model = new HashMap<>();
				boolean archived = Boolean.parseBoolean(request.queryParams("archived"));
				model.put("archive", archived);
				model.put("appeals", new PunishUtils().getAppeals(null, archived));
				model.put("utils", new PunishUtils());
				prop.put("success", true);
				prop.put("html", module.render("./source/modules/staff/appeals/appeal_list.jade", model, request, response));
				break;
			case "click-pin":
				int id = Integer.parseInt(request.queryParams("id"));
				PunishUtils.pinReport(id, username, ReportType.APPEAL);
				model = new HashMap<>();
				PunishUtils utils = new PunishUtils();
				val appeals = utils.getAppeals(username, false);
				val preports = utils.getPlayerReports(username, false);
				val breports = utils.getBugReports(username, false);
				int total = appeals.size() + preports.size() + breports.size();
				model.put("total", total);
				model.put("i_appeals", appeals);
				model.put("i_preports", preports);
				model.put("i_breports", breports);
				model.put("appeals", utils.getAppeals(null, false));
				model.put("utils", new PunishUtils());
				String html = module.render("./source/modules/staff/overview/immediate.jade", model, request, response);
				prop.put("success", true);
				prop.put("immediate", html);
				html = module.render("./source/modules/staff/appeals/appeal_list.jade", model, request, response);
				prop.put("appeals", html);
				break;
		}
		return prop;
	}
	
}
