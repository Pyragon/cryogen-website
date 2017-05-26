package com.cryo.modules.staff.appeals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.PunishmentConnection;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.support.BugReportDAO;
import com.cryo.modules.account.support.punish.AppealDAO;
import com.cryo.modules.account.support.punish.PunishDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.modules.account.support.punish.PunishUtils.ReportType;
import com.cryo.utils.CommentDAO;
import com.cryo.utils.CookieManager;

import lombok.*;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 18, 2017 at 2:09:07 AM
 */
public class StaffAppealModule {
	
	@SuppressWarnings("unchecked")
	public static ArrayList<CommentDAO> getComments(int id) {
		ArrayList<CommentDAO> comments = new ArrayList<>();
		Object[] data = PunishmentConnection.connection().handleRequest("get-comments", id, 0);
		if(data == null)
			return comments;
		return (ArrayList<CommentDAO>) data[0];
	}
	
	@SuppressWarnings("unchecked")
	public static Properties handleRequest(String action, Request request, Response response, Properties prop, WebModule module) {
		String username = CookieManager.getUsername(request);
		switch(action) {
			case "search":
				HashMap<String, Object> model = new HashMap<>();
				String text = request.queryParams("search");
				int page = Integer.parseInt(request.queryParams("page"));
				Properties search_results = Website.instance().getSearchManager().search("appeal", text, page, PunishmentConnection.connection());
				prop.put("success", search_results.get("success"));
				if(!(boolean) search_results.get("success")) {
					prop.put("error", search_results.get("error"));
					break;
				}
				List<AppealDAO> punishments = (List<AppealDAO>) search_results.get("results");
				model.put("appeals", punishments);
				String html = module.render("./source/modules/staff/appeals/appeal_list.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("pageTotal", search_results.get("pageTotal"));
				prop.put("filters", search_results.get("filters"));
				break;
			case "view-noty":
				model = new HashMap<>();
				html = module.render("./source/modules/staff/appeals/appeal_noty.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "close-appeal":
				int id = Integer.parseInt(request.queryParams("id"));
				String closeA = request.queryParams("close_action");
				String reason = "";
				if(closeA.equals("decline")) {
					reason = request.queryParams("reason");
					if(reason.equals("")) {
						prop.put("success", false);
						prop.put("error", "You must put a reason if you are declining an appeal.");
						break;
					}
				}
				int new_status = closeA.equals("decline") ? 2 : closeA.equals("accept") ? 1 : 0;
				PunishmentConnection.connection().handleRequest("close-appeal", id, new_status, username, reason);
				prop.put("success", true);
				Object[] data = PunishmentConnection.connection().handleRequest("get-appeal", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Invalid appeal ID. Please reload the page and contact an Admin if this persists.");
					break;
				}
				AppealDAO appeal = (AppealDAO) data[0];
				
				model = new HashMap<>();
				model.put("appeal", appeal);
				model.put("comments", getComments(appeal.getId()));
				html = module.render("./source/modules/staff/appeals/view_appeal.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "view-list":
				model = new HashMap<>();
				boolean archived = Boolean.parseBoolean(request.queryParams("archived"));
				page = Integer.parseInt(request.queryParams("page"));
				model.put("archive", archived);
				model.put("appeals", new PunishUtils().getAppeals(null, archived, page));
				model.put("utils", new PunishUtils());
				prop.put("success", true);
				prop.put("pageTotal", PunishUtils.getTotalPages(PunishmentConnection.connection(), "appeals"+(archived ? "-a":"")));
				prop.put("html", module.render("./source/modules/staff/appeals/appeal_list.jade", model, request, response));
				break;
			case "submit-com":
				id = Integer.parseInt(request.queryParams("id"));
				String comment = request.queryParams("comment");
				PunishmentConnection.connection().handleRequest("add-comment", username, id, 0, comment);
				model = new HashMap<>();
				model.put("comments", getComments(id));
				html = module.render("./source/modules/utils/comments.jade", model, request, response);
				prop.put("success", true);
				prop.put("comments", html);
				break;
			case "click-pin":
				id = Integer.parseInt(request.queryParams("id"));
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
				html = module.render("./source/modules/staff/overview/immediate.jade", model, request, response);
				prop.put("success", true);
				prop.put("immediate", html);
				html = module.render("./source/modules/staff/appeals/appeal_list.jade", model, request, response);
				prop.put("appeals", html);
				break;
			case "view-appeal":
				id = Integer.parseInt(request.queryParams("id"));
				data = PunishmentConnection.connection().handleRequest("get-appeal", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Invalid appeal ID. Please reload the page and contact an Admin if this persists.");
					break;
				}
				appeal = (AppealDAO) data[0];
				model = new HashMap<>();
				model.put("appeal", appeal);
				model.put("comments", getComments(appeal.getId()));
				html = module.render("./source/modules/staff/appeals/view_appeal.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
		}
		return prop;
	}
	
}
