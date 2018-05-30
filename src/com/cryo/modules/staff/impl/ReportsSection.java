package com.cryo.modules.staff.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.Noty;
import com.cryo.modules.account.entities.Report;
import com.cryo.modules.staff.entities.StaffSection;
import com.cryo.utils.CookieManager;
import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;

import spark.Request;
import spark.Response;

public class ReportsSection implements StaffSection {

	@Override
	public String getName() {
		return "reports";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Account account = CookieManager.getAccount(request);
		HashMap<String, Object> model = new HashMap<>();
		Properties prop = new Properties();
		Gson gson = Website.getGson();
		try {
			switch(action) {
			case "load":
				String html = WebModule.render("./source/modules/staff/sections/reports/reports.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "load-list":
				boolean archive = Boolean.parseBoolean(request.queryParams("archive"));
				int page = Integer.parseInt(request.queryParams("page"));
				String filter = request.queryParams("filter");
				Object[] data = ReportsConnection.connection().handleRequest("get-reports", filter, page, null, archive);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error loading reports.");
					break;
				}
				ArrayList<Report> reports = (ArrayList<Report>) data[0];
				data = ReportsConnection.connection().handleRequest("get-total-results", filter, page, null, archive);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error getting page total.");
					break;
				}
				int count = (int) data[0];
				model.put("staff", true);
				model.put("reports", reports);
				html = WebModule.render("./source/modules/account/sections/reports/reports_list.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("pageTotal", count);
				break;
			case "view-report":
				int id = Integer.parseInt(request.queryParams("id"));
				String typeName = Integer.parseInt(request.queryParams("type")) == 0 ? "bug" : "player";
				data = ReportsConnection.connection().handleRequest("get-"+typeName+"-report", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error finding report.");
					break;
				}
				Report report = (Report) data[0];
				model.put("report", report);
				model.put("comments", report.getComments());
				model.put("staff", true);
				Optional<Noty> optional = Noty.get("view_"+typeName+"_report");
				if(!optional.isPresent()) {
					prop.put("success", false);
					prop.put("error", "Unable to load noty for report.");
					break;
				}
				Noty noty = optional.get();
				html = WebModule.render(noty.getFile(), model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("active", report.isActive());
				break;
			case "comment":
				id = Integer.parseInt(request.queryParams("id"));
				typeName = Integer.parseInt(request.queryParams("type")) == 0 ? "bug" : "player";
				String comment = request.queryParams("comment");
				data = ReportsConnection.connection().handleRequest("get-"+typeName+"-report", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Unable to find report with that ID.");
					break;
				}
				report = (Report) data[0];
				if(comment.length() < 5) {
					prop.put("success", false);
					prop.put("error", "Your comment must be at least 5 characters long.");
					break;
				}
				if(StringUtils.isNullOrEmpty(comment)) {
					prop.put("success", false);
					prop.put("error", "Your comment is empty.");
					break;
				}
				int listId = report.getCommentList();
				Website.instance().getCommentsManager().addComment(account.getUsername(), comment, listId);
				ReportsConnection.connection().handleRequest("set-last-action", report.getId(), "Comment submitted by $for-name="+account.getUsername()+"$end", report.type());
				model = new HashMap<>();
				model.put("comments", report.getComments());
				html = WebModule.render("./source/modules/utils/comments.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "archive-report":
				id = Integer.parseInt(request.queryParams("id"));
				typeName = Integer.parseInt(request.queryParams("type")) == 0 ? "bug" : "player";
				data = ReportsConnection.connection().handleRequest("get-"+typeName+"-report", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Unable to find report with that ID.");
					break;
				}
				report = (Report) data[0];
				if(!report.isActive()) {
					prop.put("success", false);
					prop.put("error", "Report is already archived!");
					break;
				}
				ReportsConnection.connection().handleRequest("archive-report", id, typeName+"_reports", account.getUsername());
				prop.put("success", true);
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
			prop.put("success", false);
			prop.put("error", "Error in reports section.");
		}
		return gson.toJson(prop);
	}

}
