package com.cryo.modules.account.entities;

import java.sql.Timestamp;

import com.cryo.db.impl.EmailConnection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:55:54 PM
 */
@RequiredArgsConstructor
public class Account {
	
	private final @Getter String username;
	
	private final @Getter int id, rights, donator;
	
	private final @Getter Timestamp creationDate;
	
	public String getEmail() {
		Object[] data = EmailConnection.connection().handleRequest("get-email", username);
		if(data == null)
			return "";
		return (String) data[0];
	}
	
}