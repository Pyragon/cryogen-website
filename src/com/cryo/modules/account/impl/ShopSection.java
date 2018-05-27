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
import com.cryo.paypal.PaypalTransaction;
import com.cryo.utils.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

public class ShopSection implements AccountSection {

	@Override
	public String getName() {
		return "shop";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Properties prop = new Properties();
		Gson gson = Website.buildGson();
		if(!CookieManager.isLoggedIn(request))
			return gson.toJson(prop);
		Account account = CookieManager.getAccount(request);
		HashMap<String, Object> model = new HashMap<>();
		switch(action) {
		case "load":
			try {
				String html = WebModule.render("./source/modules/account/sections/shop/shop.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
			} catch(Exception e) {
				e.printStackTrace();
				prop.put("success", false);
				prop.put("error", "Error loading shop section.");
			}
			break;
		case "load-list":
			String filter = request.queryParams("filter");
			int page = Integer.parseInt(request.queryParams("page"));
			if(page == 0) page = 1;
			if(filter == null) {
				prop.put("success", false);
				prop.put("error", "No filter provided.");
				break;
			}
			Object[] data = ShopConnection.getShopItems(filter, page);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error loading shop items.");
				break;
			}
			HashMap<Integer, ShopItem> items = (HashMap<Integer, ShopItem>) data[0];
			int totalResults = (int) data[1];
			ArrayList<ShopItem> list = new ArrayList<ShopItem>(items.values());
			model.put("shopItems", list);
			data = ShopConnection.connection().handleRequest("get-cart", account.getUsername());
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error loading cart data!");
				break;
			}
			HashMap<String, String> cart = (HashMap<String, String>) data[0];
			String html = WebModule.render("./source/modules/account/sections/shop/shop_list.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			System.out.println(totalResults+" "+Utilities.roundUp(totalResults, 9));
			prop.put("pageTotal", Utilities.roundUp(totalResults, 9));
			int totalPrice = 0;
			int totalItems = 0;
			for(String id : cart.keySet()) {
				ShopItem item = ShopConnection.getShopItem(Integer.parseInt(id));
				if(item == null)
					continue;
				totalPrice += item.getPrice() * Integer.parseInt(cart.get(id));
				totalItems += Integer.parseInt(cart.get(id));
			}
			prop.put("totalPrice", totalPrice);
			prop.put("totalItems", totalItems);
			prop.put("items", gson.toJson(cart));
			break;
		case "load-review":
			try {
				data = ShopConnection.connection().handleRequest("get-cart", account.getUsername());
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error loading cart data!");
					break;
				}
				cart = (HashMap<String, String>) data[0];
				if(cart.size() == 0) {
					prop.put("success", false);
					prop.put("error", "You don't have any items currently in your cart!");
					break;
				}
				model.put("cart", cart);
				model.put("items", ShopConnection.getShopItems(null, -1));
				html = WebModule.render("./source/modules/account/sections/shop/review_cart.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
			} catch(Exception e) {
				e.printStackTrace();
			}
			break;
		case "set-cart":
			String cart_data = request.queryParams("cart");
			if(cart_data == null) {
				prop.put("success", false);
				prop.put("error", "No cart data provided.");
				break;
			}
			HashMap<String, String> cartItems = gson.fromJson(cart_data, HashMap.class);
			ShopConnection.connection().handleRequest("set-cart", account.getUsername(), cartItems);
			totalPrice = 0;
			totalItems = 0;
			for(String id : cartItems.keySet()) {
				ShopItem item = ShopConnection.getShopItem(Integer.parseInt(id));
				if(item == null)
					continue;
				totalPrice += item.getPrice() * Integer.parseInt(cartItems.get(id));
				totalItems += Integer.parseInt(cartItems.get(id));
			}
			prop.put("success", true);
			prop.put("totalPrice", totalPrice);
			prop.put("totalItems", totalItems);
			break;
		case "checkout":
			data = ShopConnection.connection().handleRequest("get-cart", account.getUsername());
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error loading cart data!");
				break;
			}
			cart = (HashMap<String, String>) data[0];
			if(cart.size() == 0) {
				prop.put("success", false);
				prop.put("error", "You don't have any items currently in your cart!");
				break;
			}
			HashMap<Integer, Integer> finalItems = new HashMap<Integer, Integer>();
			for(String id : cart.keySet()) {
				String quant = cart.get(id);
				finalItems.put(Integer.parseInt(id), Integer.parseInt(quant));
			}
			PaypalTransaction transaction = new PaypalTransaction(account.getUsername(), finalItems);
			prop.put("link", transaction.getLink());
			prop.put("success", true);
			break;
		}
		return gson.toJson(prop);
	}

}
