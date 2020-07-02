package com.cryo.modules.account.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.AccountSection;
import com.cryo.managers.CookieManager;
import com.cryo.utils.EmailUtils;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

public class OverviewSection implements AccountSection {

	@Override
	public String getName() {
		return "overview";
	}

	@Override
	public String decode(String action, Request request, Response response) {
		Properties prop = new Properties();
		Gson gson = new Gson();
		HashMap<String, Object> model = new HashMap<String, Object>();
		if(!CookieManager.isLoggedIn(request))
			return gson.toJson(prop);
		Account account = CookieManager.getAccount(request);
		switch(action) {
		case "load":
			try {
				Object[] data = DisplayConnection.connection().handleRequest("get-time", account.getUsername());
				int seconds = data == null ? 0 : (int) data[0];
				model.put("seconds", seconds);
				prop.put("success", true);
				String html = WebModule.render("./source/modules/account/sections/overview.jade", model, request, response);
				prop.put("html", html);
			} catch(Exception e) {
				prop.put("success", false);
				prop.put("error", "Error loading overview section");
				e.printStackTrace();
				break;
			}
			break;
		case "get-email":
			prop.put("success", true);
			prop.put("email", account.getEmail());
			break;
		case "check-email":
			String email = request.queryParams("email");
			if(email == null || email == "") {
				prop.put("success", true);
				prop.put("emailsuc", true);
				break;
			}
			boolean match = email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
			prop.put("success", true);
			prop.put("emailsuc", match);
			break;
		case "check-display":
			String name = request.queryParams("name");
			if(name == null || name == "") {
				prop.put("success", false);
				prop.put("error", "");
				break;
			}
			String result = Utilities.isValidDisplay(name.toLowerCase());
			if(result != null) {
				prop.put("success", true);
				prop.put("invalid", true);
				prop.put("error", result);
				break;
			}
			Object[] data = DisplayConnection.connection().handleRequest("name-exists", name, account.getUsername());
			if(data == null) {
				prop.put("success", false);
				prop.put("error", "Error checking display name availability. Please try again later or contact an Admin if this problem persists.");
				break;
			}
			prop.put("success", true);
			prop.put("invalid", false);
			prop.put("used", (boolean) data[0]);
			break;
		case "submit":
			HashMap<String, String> values;
			String valueString = request.queryParams("values");
			if(valueString == null || valueString.equals("")) {
				prop.put("success", false);
				prop.put("error", "No values to be changed.");
				break;
			}
			values = gson.fromJson(valueString, HashMap.class);
			
			String pass = values.get("current");
			
			data = GlobalConnection.connection().handleRequest("compare", account.getUsername(), pass);
			boolean correct = (boolean) data[0];
			
			if(!correct) {
				prop.put("success", false);
				prop.put("error", "Invalid current password!");
				break;
			}
			
			ArrayList<String> responses = new ArrayList<>();
			
			if(values.containsKey("email")) {
				email = values.get("email");
				match = email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
				if(!match)
					responses.add("Invalid email.");
				else {
					EmailUtils.sendVerificationEmail(account.getUsername(), email);
					responses.add("Verification email has been sent. Please check your email for the link to complete this request.");
				}
			}
			
			if(values.containsKey("display")) {
				String display = values.get("display");
				if(account.getDonator() == 0)
					responses.add("You must be a donator in order to change your display name.");
				else {
					result = Utilities.isValidDisplay(display.toLowerCase());
					if(result != null)
						responses.add(result);
					else {
						String old = AccountUtils.getDisplayName(account);
						DisplayConnection.connection().handleRequest("change-display", account.getUsername(), old, Utilities.formatName(display));
						data = DisplayConnection.connection().handleRequest("get-time", account.getUsername());
						prop.put("seconds", (int) data[0]);
						prop.put("displayname", AccountUtils.crownHTML(account));
						responses.add("Display name successfully changed.");
					}
				}
			}
			
			if(values.containsKey("pass") || values.containsKey("verify")) {
				String new_pass = values.get("pass");
				String verify = values.get("verify");
				if(!new_pass.equals(verify))
					responses.add("Passwords do not match!");
				else {
					data = GlobalConnection.connection().handleRequest("change-pass", account.getUsername(), new_pass, pass);
					boolean success = (boolean) data[0];
					if(!success)
						responses.add((String) data[1]);
					else
						responses.add("Password has successfully been changed.");
				}
			}
			prop.put("success", true);
			prop.put("results", gson.toJson(responses));
			break;
		}
		return gson.toJson(prop);
	}

}
