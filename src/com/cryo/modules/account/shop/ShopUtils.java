package com.cryo.modules.account.shop;

import java.util.HashMap;

import com.cryo.db.impl.ShopConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 17, 2017 at 2:12:49 AM
 */
public class ShopUtils {
	
	@SuppressWarnings("unchecked")
	public static HashMap<Integer, Integer> getCart(String username) {
		HashMap<Integer, Integer> cart = new HashMap<>();
		Object[] data = ShopConnection.connection().handleRequest("get-cart", username);
		if(data != null)
			cart = (HashMap<Integer, Integer>) data[0];
		return cart;
	}
	
	public static HashMap<Integer, Integer> getItems(String username) {
		return null;
	}
	
	public static String toString(HashMap<Integer, Integer> cart) {
		int index = 0;
		StringBuilder builder = new StringBuilder();
		for(int id : cart.keySet()) {
			ShopItem item = ShopManager.cached.get(id);
			if(item == null)
				continue;
			int amount = cart.get(id);
			builder.append(id+":"+amount+":"+item.getPrice());
			if(index+1 == cart.size())
				break;
			builder.append(",");
		}
		return builder.toString();
	}
	
	public static HashMap<Integer, Integer> fromString(String cart) {
		String[] vars = cart.split(",");
		HashMap<Integer, Integer> map = new HashMap<>();
		for(String s : vars) {
			String[] sdata = s.split(":");
			int id = Integer.parseInt(sdata[0]);
			int amount = Integer.parseInt(sdata[1]);
			map.put(id, amount);
		}
		return map;
	}
	
	public static HashMap<Integer, Integer> decreaseQuantity(String username, int id) {
		HashMap<Integer, Integer> cart = getCart(username);
		if(!cart.containsKey(id))
			return cart;
		int amount = cart.get(id);
		if(amount == 0)
			return cart;
		cart.put(id, amount-1);
		updateCart(username, cart);
		return cart;
	}
	
	public static HashMap<Integer, Integer> increaseQuantity(String username, int id) {
		HashMap<Integer, Integer> cart = getCart(username);
		if(!cart.containsKey(id)) {
			cart.put(id, 1);
			updateCart(username, cart);
			return cart;
		}
		int amount = cart.get(id);
		if(amount == 10)
			return cart;
		cart.put(id, amount+1);
		updateCart(username, cart);
		return cart;
	}
	
	public static void updateCart(String username, HashMap<Integer, Integer> cart) {
		ShopConnection.connection().handleRequest("set-cart", username, cart);
	}
	
}
