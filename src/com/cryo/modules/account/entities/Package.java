package com.cryo.modules.account.entities;

import java.sql.Timestamp;

import com.cryo.db.impl.ShopConnection;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Package {
	
	private final int id;
	
	private final String username;
	
	private final int packageId;
	
	private final String invoiceId;
	
	private final boolean active;
	
	private final Timestamp date;
	
	public ShopItem getShopItem() {
		return ShopConnection.getShopItem(packageId);
	}
	
	public Object[] data() {
		return new Object[] { "DEFAULT", username, packageId, invoiceId, active, "DEFAULT" };
	}

}
