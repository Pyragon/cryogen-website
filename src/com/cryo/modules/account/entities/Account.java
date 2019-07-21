package com.cryo.modules.account.entities;

import com.cryo.db.impl.EmailConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:55:54 PM
 */
@RequiredArgsConstructor
@Data
public class Account {

	private final String username;

	private final int id;
	private final int rights;
	private final int donator;

	private final String avatarUrl;

	private final Timestamp creationDate;
	
	public String getEmail() {
		Object[] data = EmailConnection.connection().handleRequest("get-email", username);
		if(data == null)
			return "";
		return (String) data[0];
	}
	
}
