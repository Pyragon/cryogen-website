package com.cryo.modules.login;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.AccountConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.managers.CookieManager;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Properties;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 8, 2017 at 9:44:44 AM
 */
public class LoginModule extends WebModule {
	
	public static String PATH = "/login";
	
	public static void registerEndpoints(Website website) {
		get("/login", (request, response) -> {
			if(CookieManager.isLoggedIn(request))
				return redirect("/", 0, request, response);
			HashMap<String, Object> model = new HashMap<String, Object>();
			if(request.queryParams().contains("redirect"))
				model.put("redirect", request.queryParams("redirect"));
			return render("./source/modules/account/login.jade", model, request, response);
		});
		post("/login", (request, response) -> {
			return new LoginModule(website).decodeRequest(request, response, RequestType.POST);
		});
	}
	
	public LoginModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		Properties prop = new Properties();
		HashMap<String, Object> model = new HashMap<>();
		Gson gson = Website.getGson();
		if(CookieManager.isLoggedIn(request))
			return gson.toJson(prop);
		String action = request.queryParams("action");
		switch(action) {
		case "login":
			String username = request.queryParams("username");
			String password = request.queryParams("password");
			Object[] data = GlobalConnection.connection().handleRequest("compare", username, password);
			boolean success = data == null ? false : (boolean) data[0];
			if(!success) {
				prop.put("success", false);
				prop.put("error", "Invalid username or password.");
				break;
			}
			Account account = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, username);
			if (account == null) {
				prop.put("success", false);
				prop.put("error", "Error loading account!");
				break;
			}
			data = AccountConnection.connection().handleRequest("add-sess", username);
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error adding new session.");
				break;
			}
			String session = (String) data[0];
			response.cookie("cryo-sess", session, 604_800);
			String redirect = redirect(request.queryParams("redirect"), request, response);
			prop.put("success", true);
			prop.put("html", redirect);
			break;
		case "view-login":
			String html = render("./source/modules/account/login_noty.jade", model, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		}
		return gson.toJson(prop);
	}
	
}
