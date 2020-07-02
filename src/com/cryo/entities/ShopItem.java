package com.cryo.entities;

import java.text.DecimalFormat;

public class ShopItem extends Item {

	private static final long serialVersionUID = 6386245355978648567L;

	private int price;

	public ShopItem(int id, int amount, int price) {
		super(id, amount);
		this.price = price;
	}
	
	public String formatPrice() {
		DecimalFormat format = new DecimalFormat("###,###,###");
		return format.format(price);
	}

	public int price() {
		return price;
	}

	public void set_price(int price) {
		this.price = price;
	}

	@Override
	public ShopItem clone(int amount) {
		return new ShopItem(id, amount, price);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ShopItem))
			return false;
		ShopItem item = (ShopItem) obj;
		return item.getId() == id && item.getAmount() == amount && item.price() == price;
	}

}
