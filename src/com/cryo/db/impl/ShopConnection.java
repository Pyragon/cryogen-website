package com.cryo.db.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

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
	
	public static ShopConnection connection() {
		return (ShopConnection) Website.instance().getConnectionManager().getConnection(Connection.SHOP);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "get-items":
				ArrayList<ShopItem> items = new ArrayList<>();
				ResultSet set = select("item_data", "active=?", 1);
				if(wasNull(set))
					return null;
				while(next(set)) {
					int id = getInt(set, "id");
					int price = getInt(set, "price");
					String name = getString(set, "name");
					String imageName = getString(set, "imageName");
					String type = getString(set, "type");
					String description = getString(set, "description");
					items.add(new ShopItem(id, name, imageName, price, type, description));
				}
				return new Object[] { items };
		}
		return null;
	}
	
}
