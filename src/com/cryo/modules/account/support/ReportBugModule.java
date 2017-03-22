package com.cryo.modules.account.support;

import java.util.Properties;

import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
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
		String username = request.session().attribute("cryo-user");
		String title = request.queryParams("title");
		String replicated = request.queryParams("replicated");
		String date = request.queryParams("date");
		String info = request.queryParams("info");
		Properties prop = new Properties();
		if(ReportPlayerModule.emptyOrNull(title, replicated, date, info)) {
			prop.put("success", false);
			prop.put("error", "All fields must be filled out!");
		} else {
			ReportsConnection.connection().handleRequest("report_bug", username, title, replicated, date, info);
			prop.put("success", true);
		}
		return new Gson().toJson(prop);
	}
	
}
