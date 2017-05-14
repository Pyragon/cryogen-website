package com.cryo.db.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
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

	@SuppressWarnings("unchecked")
	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "get-cart":
				String username = (String) data[1];
				HashMap<Integer, Integer> cart = new HashMap<>();
				ResultSet set = select("cart_data", "username=?", username);
				if(empty(set))
					return new Object[] { cart };
				String item_data = getString(set, "items");
				if(item_data == null || item_data.equals(""))
					return new Object[] { cart };
				cart = ShopUtils.fromStringCart(item_data);
				return new Object[] { cart };
			case "set-invoice":
				InvoiceDAO invoice = (InvoiceDAO) data[1];;
				String packages_string = ShopUtils.toJSON(invoice.getItems());
				insert("invoices", invoice.getInvoiceId(), invoice.getUsername(), packages_string, 1);
				break;
			case "get-invoice":
				String invoice_id = (String) data[1];
				boolean inactive = (Boolean) data[2];
				set = select("invoices", "id=?", invoice_id);
				if(empty(set))
					return null;
				username = getString(set, "username");
				if(inactive) {
					//We are claiming the invoice (finished paying)
					set("invoices", "active=0", "id=?", invoice_id);
					handleRequest("set-cart", username, new HashMap<Integer, Integer>());
				}
				HashMap<ShopItem, Integer> packages = ShopUtils.fromString(getString(set, "items"));
				int active = getInt(set, "active");
				invoice = new InvoiceDAO(invoice_id, username, packages, active == 1);
				return new Object[] { invoice };
			case "get-player-items":
				username = (String) data[1];
				set = select("items", "username=?", username);
				packages = new HashMap<>();
				if(empty(set))
					return null;
				packages_string = getString(set, "items");
				packages = ShopUtils.fromString(packages_string);
				return new Object[] { packages };
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
				HashMap<Integer, ShopItem> itemList = new HashMap<>();
				set = select("item_data", "active=?", 1);
				if(wasNull(set))
					return null;
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
		}
		return null;
	}
	
}
