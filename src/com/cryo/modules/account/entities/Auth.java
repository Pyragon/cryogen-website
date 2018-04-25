package com.cryo.modules.account.entities;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 12, 2017 at 11:31:33 AM
 */
@RequiredArgsConstructor
public class Auth {
	
	private final @Getter int id;
	
	private final @Getter String username, auth;
	
	private final @Getter Timestamp timestamp;
	
	public String getFormattedAuth() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < 3; i++) {
			for(int k = 0; k < 4; k++)
				builder.append(Character.toString(auth.charAt((i*4)+k)));
			if(i != 2)
				builder.append("-");
		}
		return builder.toString();
	}
	
}
