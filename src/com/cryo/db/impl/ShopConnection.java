package com.cryo.db.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.modules.account.shop.InvoiceDAO;
import com.cryo.modules.account.shop.ShopItem;
import com.cryo.modules.account.shop.ShopUtils;
import com.google.gson.Gson;
import com.paypal.api.payments.Invoice;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 14, 2017 at 12:34:57 AM
 */
public class ShopConnection extends DatabaseConnection {

	public ShopConnection() {
		super("cryogen_shop");
	}
	
	public static ShopConnection connection(Website website) {
		return (ShopConnection) website.getConnectionManager().getConnection(Connection.SHOP);
	}
	
	public static ShopConnection connection() {
		return (ShopConnection) Website.instance().getConnectionManager().getConnection(Connection.SHOP);
	}
	
	private final SQLQuery GET_CART = (set) -> {
		HashMap<Integer, Integer> cart = new HashMap<>();
		if(empty(set))
			return new Object[] { cart };
		String item_data = getString(set, "items");
		if(item_data == null || item_data.equals(""))
			return new Object[] { cart };
		cart = ShopUtils.fromStringCart(item_data);
		return new Object[] { cart };
	};
	
	private final SQLQuery GET_INVOICE = (set) -> {
		if(empty(set))
			return null;
		String id = getString(set, "id");
		String username = getString(set, "username");
		HashMap<ShopItem, Integer> packages = ShopUtils.fromString(getString(set, "items"));
		int active = getInt(set, "active");
		InvoiceDAO invoice = new InvoiceDAO(id, username, packages, active == 1);
		return new Object[] { invoice };
	};
	
	private final SQLQuery GET_PACKAGES = (set) -> {
		if(empty(set)) return null;
		String packages_string = getString(set, "items");
		HashMap<ShopItem, Integer> packages = ShopUtils.fromString(packages_string);
		return new Object[] { packages };
	};
	
	private final SQLQuery GET_SHOP_ITEMS = (set) -> {
		if(wasNull(set)) return null;
		HashMap<Integer, ShopItem> itemList = new HashMap<>();
		while(next(set)) {
			int id = getInt(set, "id");
			int price = getInt(set, "price");
			String name = getString(set, "name");
			String imageName = getString(set, "imageName");
			String type = getString(set, "type");
			String description = getString(set, "description");
			itemList.put(id, new ShopItem(id, name, imageName, price, type, description));
		}
		return new Object[] { itemList };
	};

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "get-cart":
				String username = (String) data[1];
				HashMap<Integer, Integer> cart = new HashMap<>();
				data = select("cart_data", "username=?", GET_CART, username);
				return data == null ? null : new Object[] { (HashMap<Integer, Integer>) data[0] };
			case "set-invoice":
				InvoiceDAO invoice = (InvoiceDAO) data[1];;
				String packages_string = ShopUtils.toJSON(invoice.getItems());
				insert("invoices", invoice.getInvoiceId(), invoice.getUsername(), packages_string, 1);
				break;
			case "get-invoice":
				String invoice_id = (String) data[1];
				boolean inactive = (Boolean) data[2];
				data = select("invoices", "id=?", GET_INVOICE, invoice_id);
				if(data == null) return null;
				invoice = (InvoiceDAO) data[0];
				username = invoice.getUsername();
				if(inactive) {
					//We are claiming the invoice (finished paying)
					set("invoices", "active=0", "id=?", invoice_id);
					handleRequest("set-cart", username, new HashMap<Integer, Integer>());
				}
				HashMap<ShopItem, Integer> packages = invoice.getItems();
				boolean active = invoice.isActive();
				invoice = new InvoiceDAO(invoice_id, username, packages, active);
				return new Object[] { invoice };
			case "get-player-items":
				username = (String) data[1];
				data = select("items", "username=?", GET_PACKAGES, username);
				return data == null ? null : new Object[] { (HashMap<ShopItem, Integer>) data[0] };
			case "set-player-items":
				username = (String) data[1];
				packages = (HashMap<ShopItem, Integer>) data[2];
				packages_string = ShopUtils.toJSON(packages);
				data = handleRequest("get-player-items", username);
				if(data != null)
					set("items", "items=?", "username=?", packages_string, username);
				else
					insert("items", username, packages_string);
				break;
			case "set-cart":
				username = (String) data[1];
				cart = (HashMap<Integer, Integer>) data[2];
				String json = ShopUtils.toString(cart);
				delete("cart_data", "username=?", username);
				insert("cart_data", username, json);
				break;
			case "get-items":
				data = select("item_data", "active=?", GET_SHOP_ITEMS, 1);
				return data == null ? null : new Object[] { (HashMap<Integer, ShopItem>) data[0] };
		}
		return null;
	}
	
}
