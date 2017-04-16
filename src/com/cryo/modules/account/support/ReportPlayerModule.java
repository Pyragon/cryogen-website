package com.cryo.modules.account.support;

import java.util.Properties;

import com.cryo.cookies.CookieManager;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 20, 2017 at 12:24:33 PM
 */
public class ReportPlayerModule {
	
	public static String decodeRequest(WebModule module, Request request, Response response) {
		String username = CookieManager.getUsername(request);
		Properties prop = new Properties();
		if(username.equals("")) {
			prop.put("success", false);
			prop.put("error", "Error obtaining user information. Please reload page.");
			return new Gson().toJson(prop);
		}
		String title = request.queryParams("title");
		String player = request.queryParams("offender");
		String rule = request.queryParams("rule");
		String info = request.queryParams("info");
		String proof = request.queryParams("proof");
		if(player.length() > 12) {
			prop.put("success", false);
			prop.put("error", "Offender's name cannot exceed 12 characters.");
		} else if(emptyOrNull(title, player, rule, info, proof)) {
			prop.put("success", false);
			prop.put("error", "All fields must be filled out!");
		} else {
			PlayerReportDAO report = new PlayerReportDAO(0, username, title, player, rule, info, proof, "", "", null);
			ReportsConnection.connection().handleRequest("report_player", report);
			prop.put("success", true);
		}
		return new Gson().toJson(prop);
	}
	
	public static boolean emptyOrNull(String...strings) {
		for(String string : strings)
			if(string == null || string.equals(""))
				return true;
		return false;
	}
	
}
