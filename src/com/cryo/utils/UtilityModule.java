package com.cryo.utils;

import com.cryo.Website;
import com.cryo.entities.Endpoint;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;

import java.util.HashMap;
import java.util.Properties;

public class UtilityModule {

	public static String[] ENDPOINTS = {
			"GET", "/utilities/:action",
			"POST", "/utilities/:action"
	};

	public static Endpoint decodeRequest = (endpoint, request, response) -> {
		String action = request.params(":action");
		HashMap<String, Object> model = new HashMap<>();
		Properties prop = new Properties();
		Account account = CookieManager.getAccount(request);
		switch (action) {
			case "get-restart-time":
				prop.put("success", true);
				prop.put("delay", Website.SHUTDOWN_TIME);
				break;
			case "view-noty":
				String html = WebModule.render("./source/modules/staff/sections/punishments/respond_appeal.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "clear-cache":
				if (account == null) {
					prop.put("success", false);
					prop.put("error", "You have insufficient permissions to do this.");
					break;
				}
				Website.instance().getCachingManager().clear();
				prop.put("success", true);
				break;
		}
		return Website.getGson().toJson(prop);
	};

}
