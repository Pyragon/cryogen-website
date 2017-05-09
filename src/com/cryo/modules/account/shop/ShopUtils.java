package com.cryo.modules.account.shop;

import java.util.HashMap;

import com.cryo.db.impl.ShopConnection;
import com.google.gson.Gson;

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
	
	public static String toJSON(HashMap<ShopItem, Integer> items) {
		HashMap<String, String> list = new HashMap<>();
		for(ShopItem item : items.keySet()) {
			String id = Integer.toString(item.getId());
			String quantity = Integer.toString(items.get(item));
			if(!list.containsKey(id)) {
				list.put(id, quantity);
				continue;
			}
			quantity = Integer.toString((Integer.parseInt(list.get(id)) + items.get(item)));
			list.put(id, quantity);
		}
		return new Gson().toJson(list);
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<ShopItem, Integer> fromString(String json) {
		HashMap<String, String> list = new Gson().fromJson(json, HashMap.class);
		HashMap<ShopItem, Integer> items = new HashMap<>();
		for(String id : list.keySet()) {
			int quantity = Integer.parseInt(list.get(id));
			ShopItem item = ShopManager.getShopItem(Integer.parseInt(id));
			items.put(item, quantity);
		}
		return items;
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<ShopItem, Integer> getItems(String username) {
		Object[] data = ShopConnection.connection().handleRequest("get-player-items", username);
		if(data == null)
			return new HashMap<>();
		return (HashMap<ShopItem, Integer>) data[0];
	}
	
	public static InvoiceDAO getInvoice(String invoice_id, boolean active, boolean set) {
		Object[] data = ShopConnection.connection().handleRequest("get-invoice", invoice_id, set);
		if(data == null)
			return null;
		InvoiceDAO invoice = (InvoiceDAO) data[0];
		if(invoice == null || !(active == invoice.isActive()))
			return null;
		return invoice;
	}
	
	public static void updateItems(String username, HashMap<ShopItem, Integer> items) {
		HashMap<ShopItem, Integer> current = getItems(username);
		for(ShopItem item : items.keySet()) {
			int quantity = items.get(item);
			if(!current.containsKey(item)) {
				current.put(item, quantity);
				continue;
			}
			quantity += current.get(item);
			current.put(item, quantity);
		}
		ShopConnection.connection().handleRequest("set-player-items", username, current);
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
	
	public static HashMap<Integer, Integer> fromStringCart(String cart) {
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
