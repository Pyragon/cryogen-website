package com.cryo.modules.account.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.ShopConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.AccountSection;
import com.cryo.modules.account.entities.ShopItem;
import com.cryo.modules.account.shop.ShopUtils;
import com.cryo.utils.CookieManager;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

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
				prop.put("error", "Error loading redeem section");
			}
			break;
		case "load-list":
			boolean archive = Boolean.parseBoolean(request.queryParams("archived"));
			Object[] data = ShopConnection.connection().handleRequest("get-packages", account.getUsername(), archive);
			ArrayList<com.cryo.modules.account.entities.Package> list = (ArrayList<com.cryo.modules.account.entities.Package>) data[0];
			if(list == null) {
				prop.put("success", false);
				prop.put("error", "Error loading packages.");
				break;
			}
			model.put("packages", list);
			String html = WebModule.render("./source/modules/account/sections/redeem/package_list.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "view-redeem-noty":
			int id = Integer.parseInt(request.queryParams("id"));
			html = WebModule.render("./source/modules/account/sections/redeem/redeem_noty.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "redeem-package":
			id = Integer.parseInt(request.queryParams("id"));
			try {
				data = ShopConnection.connection().handleRequest("get-package", account.getUsername(), id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "There was an error processing your package.");
					break;
				}
				com.cryo.modules.account.entities.Package packagee = (com.cryo.modules.account.entities.Package) data[0];
				if(!packagee.isActive()) {
					prop.put("success", false);
					prop.put("error", "Package has already been claimed!");
					break;
				}
				String resp = Website.instance().getPaypalManager().sendRedeem(account.getUsername(), packagee);
				if(resp != null) {
					prop.put("success", false);
					prop.put("error", "There was an error processing your package. Error: "+resp);
					break;
				}
			} catch(Exception e) {
				e.printStackTrace();
				prop.put("success", false);
				prop.put("error", "Error connecting to game server. Please try again later.");
				break;
			}
			prop.put("success", true);
		}
		return gson.toJson(prop);
	}

}
