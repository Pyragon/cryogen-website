package com.cryo.utils;

import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;

import spark.Request;
import spark.Response;
import spark.Spark;

public class UtilityModule extends WebModule {

	public UtilityModule(Website website) {
		super(website);
	}
	
	public static void registerEndpoints(Website website) {
		Spark.post("/utils", (req, res) -> new UtilityModule(website).decodeRequest(req, res, RequestType.POST));
		Spark.get("/utils", (req, res) -> new UtilityModule(website).decodeRequest(req, res, RequestType.GET));
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		String action = request.queryParams("action");
		HashMap<String, Object> model = new HashMap<>();
		Properties prop = new Properties();
		switch(action) {
			case "get-restart-time":
				prop.put("success", true);
				prop.put("delay", Website.SHUTDOWN_TIME);
				break;
		case "view-noty":
			String html = WebModule.render("./source/modules/staff/sections/punishments/respond_appeal.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		}
		return Website.getGson().toJson(prop);
	}

}
