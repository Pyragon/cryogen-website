package com.cryo.modules.account;

import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.AccountConnection;
import com.cryo.db.impl.DisplayConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

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
		if(CookieManager.isLoggedIn(request) && !request.queryParams().contains("success"))
			return redirect("/", 0, request, response);
		if(type == RequestType.POST) {
			String username = request.queryParams("username");
			username = Utilities.formatNameForProtocol(username);
			String password = request.queryParams("password");
			String passVerify = request.queryParams("passwordVerify");
			String bot = request.queryParams("bot");
			String bot2 = request.queryParams("bot2");
			String error = "";
			if(Utilities.hasNullOrEmpty(username,password,passVerify,bot,bot2))
				error = "All fields must be filled out.";
			else if(username.length() < 3 || username.length() > 12)
				error = "Usernames must be between 3 and 12 characters";
			else if(password.length() < 6 || password.length() > 20)
				error = "Passwords must be between 6 and 20 characters";
			else if(!password.equals(passVerify))
				error = "Passwords do not match.";
			else if(!bot.equals("33") || !bot2.toLowerCase().equals("red")) 
				error = "Wrong bot verification answer";
			if(!error.equals(""))
				return json(false, error);
			Object[] data = DisplayConnection.connection().handleRequest("name-exists", username, username);
			if(data == null)
				return json(false, "Error retrieving display name details");
			if((boolean) data[0])
				return json(false, "Username is already in use.");
			String valid = Utilities.isValidDisplay(username);
			if(valid != null && !valid.equals(""))
				return json(false, valid);
			GlobalConnection connection = GlobalConnection.connection();
			connection.handleRequest("register", username, password);
			data = connection.handleRequest("get-account", username);
			if(data == null)
				return redirect("/login", 0, request, response);
			Account account = (Account) data[0];
			String sess_id = (String) AccountConnection.connection().handleRequest("add-sess", username)[0];
			response.cookie("cryo-sess", sess_id);
			return json(true, redirect("/register?success", 0, request, response));
		}
		HashMap<String, Object> model = new HashMap<>();
		model.put("success", request.queryParams().contains("success"));
		return render("./source/modules/account/register.jade", model, request, response);
	}
	
	public String json(boolean success, String data) {
		Properties prop = new Properties();
		prop.put("success", success);
		if(success)
			prop.put("data", data);
		else
			prop.put("error", data);
		return new Gson().toJson(prop);
	}
	
}
