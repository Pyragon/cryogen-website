package com.cryo.modules.staff;

import java.io.IOException;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.cryo.modules.staff.entities.StaffSection;
import com.cryo.utils.CookieManager;
import com.cryo.utils.Utilities;

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
				return WebModule.showLoginPage("/staff/overview", request, response);
			StaffSection section = sections.get("overview");
			if(section == null)
				return Website.render404(request, response);
			HashMap<String, Object> model = new HashMap<>();
			model.put("section", section);
			return WebModule.render("./source/modules/staff/index.jade", model, request, response);
		});
	}
	
	private static void loadSections() {
		try {
			sections = new HashMap<>();
			for(Class<?> c : Utilities.getClasses("com.cryo.modules.staff.impl")) {
				if(c.isAnonymousClass()) continue;
				if(StaffSection.class.isAssignableFrom(c))
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
