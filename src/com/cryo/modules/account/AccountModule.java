package com.cryo.modules.account;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 11, 2017 at 11:21:26 AM
 */
public class AccountModule extends WebModule {
	
	public static String PATH = "/account";
	
	public AccountModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		if(request.session().attribute("cryo-user") == null)
			return showLoginPage("/account"+request.queryString(), request, response);
		return render("./source/modules/account/index.jade", new HashMap<>(), request, response);
	}
	
}
