package com.cryo.modules.login;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import static com.cryo.utils.Utilities.*;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 8, 2017 at 10:02:04 AM
 */
public class LogoutModule extends WebModule {
	
	public static String PATH = "/logout";

	public LogoutModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		String redirect = request.queryParams("redirect");
		request.session().removeAttribute("cryo-user");
		return redirect(redirect, request, response);
	}
	
}
