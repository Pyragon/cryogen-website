package com.cryo.modules.staff;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.Account;
import com.cryo.modules.account.AccountUtils;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 30, 2017 at 4:28:29 AM
 */
public class StaffModule extends WebModule {
	
	public static String PATH = "/staff";
	
	public StaffModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		if(request.session().attribute("cryo-user") == null)
			return showLoginPage("/staff", request, response);
		String username = request.session().attribute("cryo-user");
		Account account = AccountUtils.getAccount(username);
		if(account == null)
			return showLoginPage("/staff", request, response);
		if(account.getRights() == 0)
			return Website.render404(request, response);
		HashMap<String, Object> model = new HashMap<>();
		if(type == RequestType.GET) {
			//TODO GET SECTION
			System.out.println("lol?");
			return render("./source/modules/staff/index.jade", model, request, response);
		}
		return "hi";
	}
	
}
