package com.cryo.tasks.impl;

import com.cryo.Website;
import com.cryo.db.impl.EmailConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 13, 2017 at 3:05:39 PM
 */
@SuppressWarnings("unused")
public class EmailVerifyTask extends Task {
	
	public EmailVerifyTask() {
		super(-1, -1, -1); //-1 means will happen always. so will happen every hour, every minute, only 1 every 60 seconds
	}

	@Override
	public void run() {
		if(!Website.LOADED || EmailConnection.connection() == null) //quickfix
			return; 
		EmailConnection.connection().handleRequest("remove-verifications");
	}
	
}
