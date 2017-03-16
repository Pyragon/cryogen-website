package com.cryo.modules.account.shop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 14, 2017 at 12:30:51 AM
 */
@RequiredArgsConstructor
public class ShopItem {
	
	private final @Getter int id;
	
	private final @Getter String name, imageName;
	
	private final @Getter int price;
	
	private final @Getter String type, description;
	
	public String getImageLink() {
		return "/images/shop/"+imageName;
	}
	
}
