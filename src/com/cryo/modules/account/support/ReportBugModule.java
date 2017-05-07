package com.cryo.modules.account.support;

import java.util.Properties;

import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.cryo.utils.CookieManager;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 20, 2017 at 12:24:41 PM
 */
public class ReportBugModule {
	
	public static String decodeRequest(WebModule module, Request request, Response response) {
		String username = CookieManager.getUsername(request);
		Properties prop = new Properties();
		if(username.equals("")) {
			prop.put("success", false);
			prop.put("error", "Error obtaining user information. Please reload page.");
			return new Gson().toJson(prop);
		}
		String title = request.queryParams("title");
		String replicated = request.queryParams("replicated");
		String date = request.queryParams("date");
		String info = request.queryParams("info");
		if(ReportPlayerModule.emptyOrNull(title, replicated, date, info)) {
			prop.put("success", false);
			prop.put("error", "All fields must be filled out!");
		} else {
			BugReportDAO report = new BugReportDAO(0, username, title, replicated, date, info, null, null, null);
			ReportsConnection.connection().handleRequest("report_bug", report);
			prop.put("success", true);
		}
		return new Gson().toJson(prop);
	}
	
}
