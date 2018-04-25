package com.cryo.modules.account.entities;

import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;

import spark.Request;
import spark.Response;

public interface AccountSection {
	
	public String getName();
	
	public String decode(String action, Request request, Response response);

}
