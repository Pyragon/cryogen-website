package com.cryo.modules.account.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.ShopConnection;
import com.cryo.entities.shop.ShoppingCart;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.AccountSection;
import com.cryo.modules.account.entities.ShopItem;
import com.cryo.entities.PaypalTransaction;
import com.cryo.managers.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

import static com.cryo.Website.error;

public class ShopSection implements AccountSection {

	@Override
	public String getName() {
		return "shop";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Properties prop = new Properties();
		Gson gson = Website.buildGson();
		if(!CookieManager.isLoggedIn(request))
			return gson.toJson(prop);
		Account account = CookieManager.getAccount(request);
		HashMap<String, Object> model = new HashMap<>();
		switch(action) {
		case "load":
			try {
				String html = WebModule.render("./source/modules/account/sections/shop/shop.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
			} catch(Exception e) {
				e.printStackTrace();
				prop.put("success", false);
				prop.put("error", "Error loading shop section.");
			}
			break;
		case "load-list":
			String filter = request.queryParams("filter");
			int page = Integer.parseInt(request.queryParams("page"));
			if(page == 0) page = 1;
			if(filter == null) {
				prop.put("success", false);
				prop.put("error", "No filter provided.");
				break;
			}
			Object[] data = ShoppingCart.getShopItems(filter, page);
			if(data == null)
				return error("Error loading shop items.");
			HashMap<Integer, ShopItem> items = (HashMap<Integer, ShopItem>) data[0];
			int totalResults = (int) data[1];
			ArrayList<ShopItem> list = new ArrayList<ShopItem>(items.values());
			model.put("shopItems", list);
			ShoppingCart cart = Website.getConnection("cryogen_shop").selectClass("cart_data", "username=?", ShoppingCart.class, account.getUsername());
			if(cart == null)
				return error("Error loading shopping cart.");
			String html = WebModule.render("./source/modules/account/sections/shop/shop_list.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			prop.put("pageTotal", Utilities.roundUp(totalResults, 9));
			int totalPrice = 0;
			int totalItems = 0;
			for(String id : cart.getItems().keySet()) {
				ShopItem item = ShopConnection.getShopItem(Integer.parseInt(id));
				if(item == null)
					continue;
				totalPrice += item.getPrice() * Integer.parseInt(cart.getItems().get(id));
				totalItems += Integer.parseInt(cart.getItems().get(id));
			}
			prop.put("totalPrice", totalPrice);
			prop.put("totalItems", totalItems);
			prop.put("items", gson.toJson(cart));
			break;
		case "load-review":
			cart = Website.getConnection("cryogen_shop").selectClass("cart_data", "username=?", ShoppingCart.class, account.getUsername());
			if(cart == null)
				return error("Error loading shopping cart.");
			if(cart.getItems().size() == 0)
				return error("Your cart is empty!");
			model.put("cart", cart.getItems());
			model.put("items", ShoppingCart.getShopItems(null, -1));
			html = WebModule.render("./source/modules/account/sections/shop/review_cart.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "set-cart":
			String cartData = request.queryParams("cart");
			if(cartData == null)
				return error("Unable to parse cart data.");
			HashMap<String, String> cartItems = gson.fromJson(cartData, HashMap.class);
			cart = Website.getConnection("cryogen_shop").selectClass("cart_data", "username=?", ShoppingCart.class, account.getUsername());
			if(cart == null)
				return error("Error loading shopping cart.");
			cart.setItems(cartData);
			Website.getConnection("cryogen_shop").set("cart_data", "items=?", "id=?", cartData, cart.getId());
			totalPrice = 0;
			totalItems = 0;
			for(String id : cartItems.keySet()) {
				ShopItem item = ShopConnection.getShopItem(Integer.parseInt(id));
				if(item == null)
					continue;
				totalPrice += item.getPrice() * Integer.parseInt(cartItems.get(id));
				totalItems += Integer.parseInt(cartItems.get(id));
			}
			prop.put("success", true);
			prop.put("totalPrice", totalPrice);
			prop.put("totalItems", totalItems);
			break;
		case "checkout":
			cart = Website.getConnection("cryogen_shop").selectClass("cart_data", "username=?", ShoppingCart.class, account.getUsername());
			if(cart == null)
				return error("Error loading shopping cart.");
			if(cart.getItems().size() == 0)
				return error("Your cart is empty!");
			HashMap<Integer, Integer> finalItems = new HashMap<Integer, Integer>();
			for(String id : cart.getItems().keySet()) {
				String quant = cart.getItems().get(id);
				finalItems.put(Integer.parseInt(id), Integer.parseInt(quant));
			}
			PaypalTransaction transaction = new PaypalTransaction(account.getUsername(), finalItems);
			prop.put("link", transaction.getLink());
			prop.put("success", true);
			break;
		}
		return gson.toJson(prop);
	}

}
