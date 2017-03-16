package com.cryo.modules.account.shop;

import java.util.ArrayList;

import com.cryo.db.impl.ShopConnection;

import lombok.Getter;

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
	
}
