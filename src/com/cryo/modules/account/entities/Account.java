package com.cryo.modules.account.entities;

import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.EmailConnection;
import com.cryo.entities.CurrentDisplayName;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
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
public class Account extends MySQLDao {

	@MySQLDefault
	private final int id;
	private final String username;
	private final String password;
	private final String salt;
	private final int rights;
	private final int donator;
	private final String avatarUrl;
	@MySQLDefault
	private final Timestamp creationDate;
	
	public String getEmail() {
		Object[] data = EmailConnection.connection().handleRequest("get-email", username);
		if(data == null)
			return "";
		return (String) data[0];
	}

	public String getDisplayName() {
		CurrentDisplayName name = DisplayConnection.connection().selectClass("current_names", "username=?", CurrentDisplayName.class, username);
		if (name == null) return username;
		return name.getDisplayName();
	}
	
}
