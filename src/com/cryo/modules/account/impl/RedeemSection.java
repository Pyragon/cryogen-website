package com.cryo.modules.account.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.ShopConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.AccountSection;
import com.cryo.managers.CookieManager;
import com.cryo.modules.account.entities.Package;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

import static com.cryo.Website.error;

public class RedeemSection implements AccountSection {

	@Override
	public String getName() {
		return "redeem";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Properties prop = new Properties();
		Gson gson = new Gson();
		HashMap<String, Object> model = new HashMap<>();
		if(!CookieManager.isLoggedIn(request))
			return gson.toJson(prop);
		Account account = CookieManager.getAccount(request);
		switch(action) {
		case "load":
			try {
				String html = WebModule.render("./source/modules/account/sections/redeem/redeem.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
			} catch(Exception e) {
				e.printStackTrace();
				prop.put("success", false);
				prop.put("error", "Error loading redeem section.");
			}
			break;
		case "load-list":
			boolean archive = Boolean.parseBoolean(request.queryParams("archived"));
			ArrayList<Package> list = Website.getConnection("cryogen_shop").selectList("packages", "username=? AND active=?", Package.class, account.getUsername(), (archive ? 0 : 1));
			if(list == null)
				return error("Error loading packages.");
			model.put("packages", list);
			String html = WebModule.render("./source/modules/account/sections/redeem/package_list.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "view-redeem-noty":
			html = WebModule.render("./source/modules/account/sections/redeem/redeem_noty.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "redeem-package":
			String idString = request.queryParams("id");
			int id;
			try {
				id = Integer.parseInt(idString);
			} catch(Exception e) {
				e.printStackTrace();
				break;
			}
			Package pack = Website.getConnection("cryogen_shop").selectClass("packages", "package_id=?", Package.class, id);
			if(pack == null || !pack.isActive())
				return error("There was an error processing your package. Please refresh and try again.");
			String resp = Website.instance().getPaypalManager().sendRedeem(account.getUsername(), pack);
			if(resp != null)
				return error("There was an error processing your package. Please refresh and try again.");
			prop.put("success", true);
			break;
		}
		return gson.toJson(prop);
	}

}
