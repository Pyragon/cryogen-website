package com.cryo.modules.account.shop;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.ShopConnection;
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
	
	public ShopItem getShopItem(int id) {
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
		for(int id : cart.keySet())
			if(!items.containsKey(id))
				cart.remove(id);
		ShopConnection.connection().handleRequest("set-cart", username, cart);
		return cart;
	}
	
	@SuppressWarnings("unchecked")
	public static String processRequest(String action, Request request, Response response, RequestType type) {
		Properties prop = new Properties();
		String username = request.session().attribute("cryo-user");
		switch(action) {
			case "get-cart":
				Object[] data = ShopConnection.connection().handleRequest("get-cart", username);
				if(data == null)
					return "";
				HashMap<Integer, Integer> cart = (HashMap<Integer, Integer>) data[0];
				int index = 0;
				StringBuilder builder = new StringBuilder();
				for(int id : cart.keySet()) {
					ShopItem item = cached.get(id);
					if(item == null)
						continue;
					int amount = cart.get(id);
					builder.append(id+":"+amount+":"+item.getPrice());
					if(index+1 == cart.size())
						break;
					builder.append(",");
				}
				return builder.toString();
			case "update-cart":
				String joined = request.queryParams("cart");
				joined = joined.substring(0, joined.length()-1);
				String[] vars = joined.split(",");
				cart = new HashMap<>();
				for(String s : vars) {
					String[] sdata = s.split(":");
					int id = Integer.parseInt(sdata[0]);
					int amount = Integer.parseInt(sdata[1]);
					cart.put(id, amount);
				}
				cart = pushCartUpdate(username, cart);
				int[] cart_data = getData(cart);
				prop.put("price", cart_data[0]);
				prop.put("total", cart_data[1]);
				break;
			case "get-checkout-conf":
				String html = "";
				try {
					HashMap<String, Object> notyM = new HashMap<>();
					joined = request.queryParams("cart");
					joined = joined.substring(0, joined.length()-1);
					vars = joined.split(",");
					if(!joined.contains(",")) {
						vars = new String[1];
						vars[0] = joined;
					}
					cart = new HashMap<>();
					for(String s : vars) {
						String[] sdata = s.split(":");
						int id = Integer.parseInt(sdata[0]);
						int amount = Integer.parseInt(sdata[1]);
						cart.put(id, amount);
					}
					notyM.put("shopManager", new ShopManager());
					notyM.put("cart", cart);
					html = Jade4J.render("./source/modules/noty/shop_noty.jade", notyM);
					prop.put("html", html);
				} catch (JadeCompilerException | IOException e) {
					e.printStackTrace();
				}
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
