package com.cryo.modules.staff.playerreports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.PunishmentConnection;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountDAO;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.support.PlayerReportDAO;
import com.cryo.modules.account.support.punish.AppealDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.modules.account.support.punish.PunishUtils.ReportType;
import com.cryo.utils.CommentDAO;
import com.cryo.utils.CookieManager;
import com.cryo.utils.DateUtils;
import com.cryo.utils.Utilities;

import lombok.val;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 06, 2017 at 10:13:07 PM
 */
public class PlayerReportsModule {
	
	@SuppressWarnings("unchecked")
	public static ArrayList<CommentDAO> getComments(int id) {
		ArrayList<CommentDAO> comments = new ArrayList<>();
		Object[] data = ReportsConnection.connection().handleRequest("get-comments", id, 0);
		if(data == null)
			return comments;
		comments = (ArrayList<CommentDAO>) data[0];
		return comments;
	}
	
	@SuppressWarnings("unchecked")
	public static Properties handleRequest(String action, Request request, Response response, Properties prop, WebModule module) {
		String username = CookieManager.getUsername(request);
		switch(action) {
			case "search":
				HashMap<String, Object> model = new HashMap<>();
				String text = request.queryParams("search");
				int page = Integer.parseInt(request.queryParams("page"));
				boolean archive = Boolean.parseBoolean(request.queryParams("archive"));
				Properties search_results = Website.instance().getSearchManager().search("preport", text, page, archive, ReportsConnection.connection());
				prop.put("success", search_results.get("success"));
				if(!(boolean) search_results.get("success")) {
					prop.put("error", search_results.get("error"));
					break;
				}
				List<PlayerReportDAO> reports = (List<PlayerReportDAO>) search_results.get("results");
				model.put("preports", reports);
				String html = module.render("./source/modules/staff/player_reports/report_list.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("pageTotal", search_results.get("pageTotal"));
				prop.put("filters", search_results.get("filters"));
				break;
			case "click-pin":
				int id = Integer.parseInt(request.queryParams("id"));
				PunishUtils.pinReport(id, username, ReportType.PLAYER);
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
				model.put("preports", utils.getPlayerReports(null, false));
				model.put("utils", new PunishUtils());
				html = module.render("./source/modules/staff/overview/immediate.jade", model, request, response);
				prop.put("success", true);
				prop.put("immediate", html);
				html = module.render("./source/modules/staff/player_reports/report_list.jade", model, request, response);
				prop.put("preports", html);
				break;
			case "view-list":
				model = new HashMap<>();
				boolean archived = Boolean.parseBoolean(request.queryParams("archived"));
				page = Integer.parseInt(request.queryParams("page"));
				model.put("archive", archived);
				model.put("preports", new PunishUtils().getPlayerReports(null, archived, page));
				model.put("utils", new PunishUtils());
				prop.put("success", true);
				prop.put("pageTotal", PunishUtils.getTotalPages(ReportsConnection.connection(), archived ? "p_archive" : "player_reports"));
				prop.put("html", module.render("./source/modules/staff/player_reports/report_list.jade", model, request, response));
				break;
			case "view-report":
				id = Integer.parseInt(request.queryParams("id"));
				archived = Boolean.parseBoolean(request.queryParams("archived"));
				prop = viewReport(id, archived, request, response, prop, module);
				break;
			case "archive-report":
				id = Integer.parseInt(request.queryParams("id"));
				ReportsConnection.connection().handleRequest("archive-report", id, 0, username);
				prop.put("success", true);
				model = new HashMap<>();
				model.put("preports", new PunishUtils().getPlayerReports(username, false));
				prop.put("html", module.render("./source/modules/staff/player_reports/report_list.jade", model, request, response));
				break;
			case "submit-com":
				id =  Integer.parseInt(request.queryParams("id"));
				String comment = request.queryParams("comment");
				Object[] data = ReportsConnection.connection().handleRequest("submit-com", id, 0, username, comment);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error submitting comment. Please try again later and contact an Admin if the problem persists.");
					break;
				}
				model = new HashMap<>();
				model.put("comments", getComments(id));
				html = module.render("./source/modules/utils/comments.jade", model, request, response);
				prop.put("success", true);
				prop.put("comments", html);
				break;
		}
		return prop;
	}
	
	public static Properties viewReport(int id, boolean archived, Request request, Response response, Properties prop, WebModule module) {
		Object[] data = ReportsConnection.connection().handleRequest("get-player-report", id, archived);
		if(data == null) {
			prop.put("success", false);
			prop.put("error",  "Report not found.");
			return prop;
		}
		System.out.println("ID: "+id);
		HashMap<String, Object> model = new HashMap<>();
		PlayerReportDAO report = (PlayerReportDAO) data[0];
		model.put("report",  report);
		ArrayList<CommentDAO> comments = getComments(report.getId());
		System.out.println("Comments: "+comments.size());
		model.put("comments", comments);
		String html = module.render("./source/modules/staff/player_reports/view_report.jade", model, request, response);
		AccountDAO account = AccountUtils.getAccount(report.getUsername());
		String name = AccountUtils.crownHTML(account);
		prop.put("success", true);
		prop.put("html", html);
		prop.put("display", name);
		return prop;
	}
	
}
