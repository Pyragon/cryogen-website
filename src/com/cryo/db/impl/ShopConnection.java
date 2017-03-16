package com.cryo.db.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.modules.account.shop.ShopItem;

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
				if(item_data == null || item_data == "")
					return new Object[] { cart };
				String[] items = item_data.split(",");
				for(String item : items) {
					String[] sdata = item.split(":");
					int id = Integer.parseInt(sdata[0]);
					int amount = Integer.parseInt(sdata[1]);
					cart.put(id, amount);
				}
				return new Object[] { cart };
			case "set-cart":
				username = (String) data[1];
				cart = (HashMap<Integer, Integer>) data[2];
				int index = 0;
				StringBuilder builder = new StringBuilder();
				for(int id : cart.keySet()) {
					int amount = cart.get(id);
					builder.append(id+":"+amount);
					if(index+1 == cart.size())
						break;
					builder.append(",");
				}
				delete("cart_data", "username=?", username);
				insert("cart_data", username, builder.toString());
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
