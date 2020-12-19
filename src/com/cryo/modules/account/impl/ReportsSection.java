package com.cryo.modules.account.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

import com.cryo.db.impl.CommentsConnection;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.AccountSection;
import com.cryo.modules.account.entities.BugReport;
import com.cryo.modules.account.entities.Noty;
import com.cryo.modules.account.entities.PlayerReport;
import com.cryo.modules.account.entities.Report;
import com.cryo.managers.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

public class ReportsSection implements AccountSection {

	@Override
	public String getName() {
		return "reports";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Properties prop = new Properties();
		Gson gson = new Gson();
		try {
			HashMap<String, Object> model = new HashMap<String, Object>();
			if(!CookieManager.isLoggedIn(request))
				return gson.toJson(prop);
			Account account = CookieManager.getAccount(request);
			switch(action) {
			case "load":
				try {
					String html = WebModule.render("./source/modules/account/sections/reports/reports.jade", model, request, response);
					prop.put("success", true);
					prop.put("html", html);
				} catch(Exception e) {
					e.printStackTrace();
					prop.put("success", false);
					prop.put("error", "Error loading reports section");
				}
				break;
			case "load-list":
				String type = request.queryParams("type");
				String filter = request.queryParams("filters");
				int page = Integer.parseInt(request.queryParams("page"));
				boolean archived = Boolean.parseBoolean(request.queryParams("archive"));
				ArrayList<Report> reports;
				HashMap<String, String> filters = gson.fromJson(filter, HashMap.class);
				if(filters.size() == 0) {
					Object[] data = ReportsConnection.connection().handleRequest("get-reports", type, page, account.getUsername(), archived);
					if(data == null) {
						prop.put("success", false);
						prop.put("error", "Error retreiving reports.");
						break;
					}
					reports = (ArrayList<Report>) data[0];
					data = ReportsConnection.connection().handleRequest("get-total-results", type, page, account.getUsername(), archived);
					if(data == null) {
						prop.put("success", false);
						prop.put("error", "Error retreiving result total.");
						break;
					}
					int count = (int) data[0];
					model.put("reports", reports);
					try {
						String html = WebModule.render("./source/modules/account/sections/reports/reports_list.jade", model, request, response);
						prop.put("success", true);
						prop.put("html", html);
						prop.put("pageTotal", count);
						break;
					} catch(Exception e) {
						e.printStackTrace();
					}
					break;
				}
				break;
			case "load-noty":
				String name = request.queryParams("name");
				Optional<Noty> optional = Noty.get(name);
				if(!optional.isPresent()) {
					prop.put("success", false);
					prop.put("error", "Unable to load noty for that name.");
					break;
				}
				Noty noty = optional.get();
				prop.put("success", true);
				prop.put("html", WebModule.render(noty.getFile(), model, request, response));
				break;
			case "archive-report":
				int id = Integer.parseInt(request.queryParams("id"));
				String typeName = Integer.parseInt(request.queryParams("type")) == 0 ? "bug" : "player";
				Object[] data = ReportsConnection.connection().handleRequest("get-"+typeName+"-report", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Unable to find report with that ID.");
					break;
				}
				Report report = (Report) data[0];
				if(!report.isActive()) {
					prop.put("success", false);
					prop.put("error", "Report is already archived!");
					break;
				}
				ReportsConnection.connection().handleRequest("archive-report", id, typeName+"_reports", account.getUsername());
				prop.put("success", true);
				break;
			case "view-report":
				id = Integer.parseInt(request.queryParams("id"));
				typeName = Integer.parseInt(request.queryParams("type")) == 0 ? "bug" : "player";
				data = ReportsConnection.connection().handleRequest("get-"+typeName+"-report", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Unable to find report with that ID.");
					break;
				}
				report = (Report) data[0];
				if(!report.getUsername().equals(account.getUsername())) {
					prop.put("success", false);
					prop.put("error", "You can only view your own reports!");
					break;
				}
				model = new HashMap<>();
				model.put("report", report);
				model.put("list", report.getCommentList());
				optional = Noty.get("view_"+typeName+"_report");
				if(!optional.isPresent()) {
					prop.put("success", false);
					prop.put("error", "Unable to load noty for report.");
					break;
				}
				noty = optional.get();
				String html = WebModule.render(noty.getFile(), model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("active", report.isActive());
				break;
			case "create-player-report":
				String title = request.queryParams("title");
				String offender = request.queryParams("offender");
				String rule = request.queryParams("rule");
				String proof = request.queryParams("proof");
				String info = request.queryParams("info");
				if(info == null)
					info = "";
				if(Utilities.isNullOrEmpty(title, offender, rule, proof)) {
					prop.put("success", false);
					prop.put("error", "Please fill out all required fields.");
					break;
				}
				data = CommentsConnection.connection().handleRequest("add-comment-list");
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error adding comment list.");
					break;
				}
				int commentList = (int) data[0];
				report = new PlayerReport(0, account.getUsername(), title, offender, rule, proof, info, "Created", commentList, null, null, true);
				ReportsConnection.connection().handleRequest("report-player", report);
				prop.put("success", true);
				break;
			case "create-bug-report":
				title = request.queryParams("title");
				String replicate = request.queryParams("replicate");
				String seen = request.queryParams("seen");
				info = request.queryParams("info");
				if(info == null) info = "";
				if(Utilities.isNullOrEmpty(title, replicate, seen)) {
					prop.put("success", false);
					prop.put("error", "Please fill out all required fields.");
					break;
				}
				data = CommentsConnection.connection().handleRequest("add-comment-list");
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error adding comment list.");
					break;
				}
				commentList = (int) data[0];
				BugReport bug = new BugReport(0, account.getUsername(), title, replicate, seen, info, "Created", commentList, null, null, true);
				ReportsConnection.connection().handleRequest("report-bug", bug);
				prop.put("success", true);
				break;
			}
		} catch(Exception e) {
			prop.put("success", false);
			prop.put("error", "Error in reports section");
			e.printStackTrace();
		}
		return gson.toJson(prop);
	}

}
