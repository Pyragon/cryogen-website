package com.cryo.modules.account.entities;

import java.sql.Timestamp;
import java.util.HashMap;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 07, 2017 at 4:03:28 AM
 */
@RequiredArgsConstructor
@Data
public class Invoice {
	
	private final String invoiceId, username;
	
	private final HashMap<ShopItem, Integer> items;
	
	private final boolean active;
	
	private final Timestamp date;
	
}
