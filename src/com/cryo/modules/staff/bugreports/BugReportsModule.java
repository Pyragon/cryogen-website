package com.cryo.modules.staff.bugreports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.cookies.CookieManager;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.support.BugReportDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.modules.account.support.punish.PunishUtils.ReportType;
import com.cryo.utils.CommentDAO;
import com.cryo.utils.DateFormatter;

import lombok.*;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 09, 2017 at 8:44:18 PM
 */
public class BugReportsModule {
	
	@SuppressWarnings("unchecked")
	public static ArrayList<CommentDAO> getComments(int id) {
		ArrayList<CommentDAO> comments = new ArrayList<>();
		Object[] data = ReportsConnection.connection().handleRequest("get-comments", id, 1);
		if(data == null)
			return comments;
		return (ArrayList<CommentDAO>) data[0];
	}
	
	public static Properties handleRequest(String action, Request request, Response response, Properties prop, WebModule module) {
		String username = CookieManager.getUsername(request);
		switch(action) {
			case "click-pin":
				int id = Integer.parseInt(request.queryParams("id"));
				PunishUtils.pinReport(id, username, ReportType.BUG);
				HashMap<String, Object> model = new HashMap<>();
				PunishUtils utils = new PunishUtils();
				val appeals = utils.getAppeals(username);
				val preports = utils.getPlayerReports(username, false);
				val breports = utils.getBugReports(username);
				int total = appeals.size() + preports.size() + breports.size();
				model.put("total", total);
				model.put("i_appeals", appeals);
				model.put("i_preports", preports);
				model.put("i_breports", breports);
				model.put("breports", utils.getBugReports(null));
				model.put("utils", new PunishUtils());
				String html = module.render("./source/modules/staff/overview/immediate.jade", model, request, response);
				prop.put("success", true);
				prop.put("immediate", html);
				html = module.render("./source/modules/staff/bug_reports/report_list.jade", model, request, response);
				prop.put("breports", html);
				break;
			case "view-report":
				id = Integer.parseInt(request.queryParams("id"));
				Object[] data = ReportsConnection.connection().handleRequest("get-bug-report", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Invalid report ID. Please reload the page and contact an Admin if this persists.");
					break;
				}
				BugReportDAO report = (BugReportDAO) data[0];
				model = new HashMap<>();
				model.put("report", report);
				model.put("comments", getComments(report.getId()));
				html = module.render("./source/modules/staff/bug_reports/view_report.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "archive-report":
				id = Integer.parseInt(request.queryParams("id"));
				ReportsConnection.connection().handleRequest("archive-report", id, 1);
				prop.put("success", true);
				model = new HashMap<>();
				model.put("breports", new PunishUtils().getBugReports(username));
				prop.put("html", module.render("./source/modules/staff/bug_reports/report_list.jade", model, request, response));
				break;
			case "submit-com":
				id = Integer.parseInt(request.queryParams("id"));
				String comment = request.queryParams("comment");
				ReportsConnection.connection().handleRequest("submit-com", id, 1, username, comment);
				model = new HashMap<>();
				model.put("comments", getComments(id));
				html = module.render("./source/modules/utils/comments.jade", model, request, response);
				prop.put("success", true);
				prop.put("comments", html);
				break;
		}
		return prop;
	}
	
}
