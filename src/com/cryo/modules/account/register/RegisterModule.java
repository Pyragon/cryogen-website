package com.cryo.modules.account.register;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 09, 2017 at 2:02:38 AM
 */
public class RegisterModule extends WebModule {
	
	public static String PATH = "/register";
	
	public RegisterModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		if(request.session().attributes().contains("cryo-user")) {
			redirect("/", 0, request, response);
			return null;
		}
		HashMap<String, Object> model = new HashMap<>();
		model.put("success", true);
		return render("./source/modules/account/register.jade", model, request, response);
	}
	
}
