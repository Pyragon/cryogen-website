package com.cryo.modules.account;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.AccountSection;
import com.cryo.utils.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class AccountModule extends WebModule {
	
	private static HashMap<String, AccountSection> sections;
	
	static {
		loadSections();
	}

	public AccountModule(Website website) {
		super(website);
	}

	@Override
	public Object decodeRequest(Request request, Response response, RequestType type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void registerEndpoints(Website website) {
		get("/account", (request, response) -> {
			if(!CookieManager.isLoggedIn(request))
				return WebModule.showLoginPage("/account/overview", request, response);
			AccountSection section = sections.get("overview");
			if(section == null)
				return Website.render404(request, response);
			HashMap<String, Object> model = new HashMap<>();
			model.put("section", section);
			return WebModule.render("./source/modules/account/index.jade", model, request, response);
		});
		get("/account/:section", (request, response) -> {
			String name = request.params(":section");
			AccountSection section = null;
			if(name == null || (section = sections.get(name)) == null)
				return Website.render404(request, response);
			if(!CookieManager.isLoggedIn(request))
				return WebModule.showLoginPage("/account/"+name, request, response);
			HashMap<String, Object> model = new HashMap<>();
			model.put("section", section);
			return WebModule.render("./source/modules/account/index.jade", model, request, response);
		});
		post("/account/:section", (request, response) -> {
			String sectionName = request.params(":section");
			String action = request.queryParams("action");
			Properties prop = new Properties();
			Gson gson = new Gson();
			if(sectionName == null || action == null) {
				prop.put("success", false);
				prop.put("error", "Section or Action is null.");
				return gson.toJson(prop);
			}
			AccountSection section = sections.get(sectionName);
			if(section == null) {
				prop.put("success", false);
				prop.put("error", "Unable to find section.");
				return gson.toJson(prop);
			}
			return section.decode(action, request, response);
		});
	}
	
	private static void loadSections() {
		try {
			sections = new HashMap<>();
			for(Class<?> c : Utilities.getClasses("com.cryo.modules.account.impl")) {
				if(c.isAnonymousClass()) continue;
				if(AccountSection.class.isAssignableFrom(c))
					continue;
				Object o = c.newInstance();
				if(!(o instanceof AccountSection))
					continue;
				AccountSection section = (AccountSection) o;
				sections.put(section.getName(), section);
			}
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
