package com.cryo.modules.account.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website.RequestType;
import com.cryo.db.impl.ShopConnection;
import com.google.gson.Gson;

import lombok.Getter;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 14, 2017 at 12:30:18 AM
 */
public class ShopManager {
	
	private @Getter ArrayList<ShopItem> items;
	
	public ShopManager() {
		load();
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		Object[] data = ShopConnection.connection().handleRequest("get-items");
		if(data == null) {
			items = new ArrayList<>();
			return;
		}
		items = (ArrayList<ShopItem>) data[0];
	}
	
	@SuppressWarnings("unchecked")
	public static void pushCartUpdate(String username, HashMap<Integer, Integer> items) {
		Object[] data = ShopConnection.connection().handleRequest("get-cart", username);
		HashMap<Integer, Integer> cart = new HashMap<Integer, Integer>();
		if(data != null)
			cart = (HashMap<Integer, Integer>) data[0];
		for(int id : items.keySet()) {
			if(!cart.containsKey(id)) {
				cart.put(id, items.get(id));
				continue;
			}
		}
	}
	
	public static String processRequest(String action, Request request, Response response, RequestType type) {
		Properties prop = new Properties();
		switch(action) {
			case "update-cart":
				String joined = request.queryParams("cart");
				joined = joined.substring(1);
				String[] vars = joined.split(",");
				break;
		}
		return new Gson().toJson(prop);
	}
	
}
