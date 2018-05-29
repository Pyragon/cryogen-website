package com.cryo.modules.staff;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.staff.entities.StaffSection;
import com.cryo.utils.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

import static spark.Spark.get;
import static spark.Spark.post;

public class StaffModule extends WebModule {
	
	private static HashMap<String, StaffSection> sections;
	
	static {
		loadSections();
	}

	public StaffModule(Website website) {
		super(website);
	}

	@Override
	public Object decodeRequest(Request request, Response response, RequestType type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void registerEndpoints(Website website) {
		get("/staff", (request, response) -> {
			if(!CookieManager.isLoggedIn(request))
				return WebModule.showLoginPage("/staff/announcements", request, response);
			Account account = CookieManager.getAccount(request);
			if(account == null || account.getRights() == 0)
				return Website.render404(request, response);
			StaffSection section = sections.get("announcements");
			if(section == null)
				return Website.render404(request, response);
			HashMap<String, Object> model = new HashMap<>();
			model.put("section", section);
			return WebModule.render("./source/modules/staff/index.jade", model, request, response);
		});
		get("/staff/:section", (request, response) -> {
			String name = request.params(":section");
			StaffSection section = null;
			if(name == null || (section = sections.get(name)) == null)
				return Website.render404(request, response);
			if(!CookieManager.isLoggedIn(request))
				return WebModule.showLoginPage("/staff/"+name, request, response);
			Account account = CookieManager.getAccount(request);
			if(account == null || account.getRights() == 0)
				return Website.render404(request, response);
			HashMap<String, Object> model = new HashMap<>();
			model.put("section", section);
			return WebModule.render("./source/modules/staff/index.jade", model, request, response);
		});
		post("/staff/:section", (request, response) -> {
			String name = request.params(":section");
			String action = request.queryParams("action");
			Gson gson = Website.getGson();
			Properties prop = new Properties();
			while(true) {
				if(name == null) {
					prop.put("success", false);
					prop.put("error", "No section provided");
					break;
				}
				if(!CookieManager.isLoggedIn(request))
					return WebModule.showLoginPage("/staff/"+name+"/"+request.queryString(), request, response);
				Account account = CookieManager.getAccount(request);
				if(account == null || account.getRights() == 0) {
					prop.put("success", false);
					prop.put("error", "Invalid account or insufficient permissions.");
					break;
				}
				StaffSection section = sections.get(name);
				if(section == null) {
					prop.put("success", false);
					prop.put("error", "Error loading section.");
					break;
				}
				return section.decode(action, request, response);
			}
			return gson.toJson(prop);
		});
	}
	
	private static void loadSections() {
		try {
			sections = new HashMap<>();
			for(Class<?> c : Utilities.getClasses("com.cryo.modules.staff.impl")) {
				if(c.isAnonymousClass()) continue;
				if(!StaffSection.class.isAssignableFrom(c))
					continue;
				Object o = c.newInstance();
				if(!(o instanceof StaffSection))
					continue;
				StaffSection section = (StaffSection) o;
				sections.put(section.getName(), section);
			}
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
