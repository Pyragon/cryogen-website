package com.cryo.modules.account;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.vote.VotingManager;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 11, 2017 at 11:21:26 AM
 */
public class AccountOverviewModule extends WebModule {
	
	public static String PATH = "/account";
	
	public AccountOverviewModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		if(request.session().attribute("cryo-user") == null)
			return showLoginPage("/account?"+request.queryString(), request, response);
		HashMap<String, Object> model = new HashMap<>();
		if(request.queryParams().contains("section"))
			model.put("section", request.queryParams("section"));
		String username = request.session().attribute("cryo-user");
		model.put("voteManager", new VotingManager(username));
		return render("./source/modules/account/index.jade", model, request, response);
	}
	
}