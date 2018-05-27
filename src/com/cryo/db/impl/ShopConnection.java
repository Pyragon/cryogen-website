package com.cryo.db.impl;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.modules.account.entities.Invoice;
import com.cryo.modules.account.entities.ShopItem;
import com.cryo.modules.account.shop.ShopUtils;
import com.google.gson.Gson;

import lombok.Getter;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 14, 2017 at 12:34:57 AM
 */
public class ShopConnection extends DatabaseConnection {
	
	public static HashMap<Integer, ShopItem> cached;
	
	@SuppressWarnings("unchecked")
	public static void load(Website website) {
		Object[] data = connection(website).handleRequest("get-items");
		if(data == null) {
			cached = new HashMap<>();
			System.out.println("nulll");
			return;
		}
		cached = (HashMap<Integer, ShopItem>) data[0];
	}
	
	public static ShopItem getShopItem(int id) {
		return cached.get(id);
	}
	
	public static Object[] getShopItems(String filter, int page) {
		HashMap<Integer, ShopItem> items = new HashMap<>();
		if(filter == null || filter.equals("All")) {
			if(page == -1)
				return new Object[] { cached, cached.size() };
			cached.values().stream().skip((page-1)*9).limit(9).forEach(s -> items.put(s.getId(), s));
			return new Object[] { items, cached.size() };
		}
		Supplier<Stream<ShopItem>> supplier = () -> cached.values().stream().filter(s -> s.getType().equals(filter));
		int count = (int) supplier.get().count();
		if(page > 0)
			supplier.get().skip((page-1)*9).limit(9).forEach(s -> items.put(s.getId(), s));
		else
			supplier.get().forEach(s -> items.put(s.getId(), s));
		return new Object[] { items, count };
	}

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
		HashMap<String, String> cart = new HashMap<>();
		if(empty(set)) return new Object[] { cart };
		String item_data = getString(set, "items");
		if(item_data == null || item_data.equals(""))
			return new Object[] { cart };
		cart = new Gson().fromJson(item_data, HashMap.class);
		return new Object[] { cart };
	};
	
	private final SQLQuery GET_INVOICE = (set) -> {
		if(empty(set))
			return null;
		String id = getString(set, "id");
		String username = getString(set, "username");
		HashMap<ShopItem, Integer> packages = ShopUtils.fromString(getString(set, "items"));
		int active = getInt(set, "active");
		Timestamp date = getTimestamp(set, "date");
		Invoice invoice = new Invoice(id, username, packages, active == 1, date);
		return new Object[] { invoice };
	};
	
	private final SQLQuery GET_PACKAGES = (set) -> {
		if(wasNull(set)) return new Object[] { new ArrayList<>() };
		ArrayList<com.cryo.modules.account.entities.Package> list = new ArrayList<>();
		while(next(set)) {
			list.add(loadPackage(set));
		}
		return new Object[] { list };
	};
	
	private final SQLQuery GET_PACKAGE = (set) -> {
		if(empty(set)) return null;
		return new Object[] { loadPackage(set) };
	};
	
	private com.cryo.modules.account.entities.Package loadPackage(ResultSet set) {
		int id = getInt(set, "id");
		String username = getString(set, "username");
		int packageId = getInt(set, "package_id");
		String invoiceId = getString(set, "invoice_id");
		boolean active = getInt(set, "active") == 1 ? true : false;
		Timestamp date = getTimestamp(set, "date");
		Timestamp redeemDate = getTimestamp(set, "redeem_date");
		return new com.cryo.modules.account.entities.Package(id, username, packageId, invoiceId, active, date, redeemDate);
	}
	
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
			case "search-results":
				Properties queryValues = (Properties) data[1];
				HashMap<String, String> params = (HashMap<String, String>) data[3];
				String type = params.get("type");
				String query = (String) queryValues.getProperty("query");
				Object[] values = (Object[]) queryValues.get("values");
				ArrayList<Object> objs = new ArrayList<>(Arrays.asList(values));
				if(!type.equals("All")) {
					query += " AND type=?";
					objs.add(type);
				}
				return new Object[] { selectCount("item_data", query, objs.toArray()) };
			case "search":
				queryValues = (Properties) data[1];
				int page = (int) data[2];
				params = (HashMap<String, String>) data[4];
				type = params.get("type");
				query = (String) queryValues.getProperty("query");
				values = (Object[]) queryValues.get("values");
				if(page == 0)
					page = 1;
				int offset = (page - 1) * 10;
				objs = new ArrayList<>(Arrays.asList(values));
				if(!type.equals("All")) {
					query += " AND type=?";
					objs.add(type);
				}
				query += "LIMIT ?,10";
				objs.add(offset);
				List<ShopItem> items = new ArrayList<>();
				data = select("item_data", query, GET_SHOP_ITEMS, objs.toArray());
				if(data == null) return new Object[] { items };
				HashMap<Integer, ShopItem> shopItems = (HashMap<Integer, ShopItem>) data[0];
				items = shopItems.values().stream().collect(Collectors.toList());
				return new Object[] { items };
			case "get-cart":
				return select("cart_data", "username=?", GET_CART, (String) data[1]);
			case "set-cart":
				String username = (String) data[1];
				delete("cart_data", "username=?", username);
				insert("cart_data", username, new Gson().toJson((HashMap<String, String>) data[2]));
				break;
			case "set-invoice":
				Invoice invoice = (Invoice) data[1];;
				String packages_string = ShopUtils.toJSON(invoice.getItems());
				insert("invoices", invoice.getInvoiceId(), invoice.getUsername(), packages_string, 1, "DEFAULT");
				break;
			case "get-invoice":
				String invoice_id = (String) data[1];
				boolean inactive = (Boolean) data[2];
				data = select("invoices", "id=?", GET_INVOICE, invoice_id);
				if(data == null) return null;
				invoice = (Invoice) data[0];
				username = invoice.getUsername();
				if(inactive) {
					//We are claiming the invoice (finished paying)
					set("invoices", "active=0", "id=?", invoice_id);
					handleRequest("set-cart", username, new HashMap<Integer, Integer>());
				}
				return new Object[] { invoice };
			case "get-package":
				return select("packages", "username=? AND id=?", GET_PACKAGE, (String) data[1], (int) data[2]);
			case "get-packages":
				username = (String) data[1];
				boolean archive = (boolean) data[2];
				return select("packages", "username=? AND active=?", GET_PACKAGES, username, archive == true ? 0 : 1);
			case "add-package":
				com.cryo.modules.account.entities.Package purchasedPackage = (com.cryo.modules.account.entities.Package) data[1];
				insert("packages", purchasedPackage.data());
				break;
			case "get-items":
				data = select("item_data", "active=?", GET_SHOP_ITEMS, 1);
				return data == null ? null : new Object[] { (HashMap<Integer, ShopItem>) data[0] };
		}
		return null;
	}
	
}
