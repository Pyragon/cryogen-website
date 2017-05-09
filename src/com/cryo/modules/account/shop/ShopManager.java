package com.cryo.modules.account.shop;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.ShopConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.Account;
import com.cryo.paypal.PaypalTransaction;
import com.cryo.utils.CookieManager;
import com.google.gson.Gson;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import lombok.Getter;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 14, 2017 at 12:30:18 AM
 */
public class ShopManager {
	
	public static HashMap<Integer, ShopItem> cached;
	
	@SuppressWarnings("unchecked")
	public static void load(Website website) {
		Object[] data = ShopConnection.connection(website).handleRequest("get-items");
		if(data == null) {
			cached = new HashMap<>();
			return;
		}
		cached = (HashMap<Integer, ShopItem>) data[0];
	}
	
	public ShopManager() {
	}
	
	public static ShopItem getShopItem(int id) {
		return cached.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<Integer, Integer> pushCartUpdate(String username, HashMap<Integer, Integer> items) {
		Object[] data = ShopConnection.connection().handleRequest("get-cart", username);
		HashMap<Integer, Integer> cart = new HashMap<Integer, Integer>();
		if(data != null)
			cart = (HashMap<Integer, Integer>) data[0];
		for(int id : items.keySet()) {
			if(!cart.containsKey(id)) {
				cart.put(id, items.get(id));
				continue;
			}
			int amount = cart.get(id);
			if(amount != items.get(id))
				amount = items.get(id);
			cart.put(id, amount);
		}
		for(int id : items.keySet())
			if(!items.containsKey(id))
				cart.remove(id);
		ShopConnection.connection().handleRequest("set-cart", username, cart);
		return cart;
	}
	
	public static String processRequest(String action, Request request, Response response, RequestType type, WebModule module) {
		Properties prop = new Properties();
		Account account = CookieManager.getAccount(request);
		String username = account.getUsername();
		switch(action) {
			case "chg-quant":
				int id = Integer.parseInt(request.queryParams("id"));
				int quant = Integer.parseInt(request.queryParams("quant"));
				String carts = "";
				if(quant == 1)
					carts = ShopUtils.toString(ShopUtils.increaseQuantity(username, id));
				else
					carts = ShopUtils.toString(ShopUtils.decreaseQuantity(username, id));
				prop.put("cart", carts);
				break;
			case "get-cart":
				String ret = ShopUtils.toString(ShopUtils.getCart(username));
				return ret;
			case "view-packages":
				HashMap<String, Object> model = new HashMap<>();
				model.put("packages", ShopUtils.getItems(username));
				String html = module.render("./source/modules/account/sections/redeem/packages.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "redeem-noty":
				html = module.render("./source/modules/account/sections/redeem/redeem_noty.jade", new HashMap<>(), request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "redeem":
				id = Integer.parseInt(request.queryParams("id"));
				try {
					Website.instance().getPaypalManager().sendRedeem(username, id);
				} catch(Exception e) {
					e.printStackTrace();
					prop.put("success", false);
					prop.put("error", e.getMessage());
					break;
				}
				model = new HashMap<>();
				model.put("packages", ShopUtils.getItems(username));
				html = module.render("./source/modules/account/sections/redeem/packages.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "confirm":
				PaypalTransaction transaction = new PaypalTransaction(username, ShopUtils.getCart(username));
				prop.put("link", transaction.getLink());
				break;
			case "get-checkout-conf":
				HashMap<String, Object> notyM = new HashMap<>();
				HashMap<Integer, Integer> cart = ShopUtils.getCart(username);
				notyM.put("shopManager", new ShopManager());
				notyM.put("cart", cart);
				html = module.render("./source/modules/account/sections/shop/shop_noty.jade", notyM, request, response);
				prop.put("html", html);
				break;
		}
		return new Gson().toJson(prop);
	}
	
	public static int[] getData(HashMap<Integer, Integer> cart) {
		int price = 0;
		int total = 0;
		for(int id : cart.keySet()) {
			ShopItem item = cached.get(id);
			if(item == null)
				continue;
			int amount = cart.get(id);
			price += (item.getPrice()*amount);
			total += amount;
		}
		return new int[] { price, total };
	}
	
}
