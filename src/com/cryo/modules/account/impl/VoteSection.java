package com.cryo.modules.account.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.entities.AccountSection;
import com.cryo.modules.account.entities.Auth;
import com.cryo.managers.CookieManager;
import com.cryo.managers.VotingManager;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

public class VoteSection implements AccountSection {

	@Override
	public String getName() {
		return "vote";
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
				ArrayList<Auth> auths = new ArrayList<>();
				HashMap<Integer, Integer> times = new HashMap<>();
				for(int i = 0; i < 3; i++)
					times.put(i, VotingManager.getTime(account.getUsername(), i));
				model.put("times", times);
				model.put("manager", new VotingManager());
				model.put("auths", auths);
				String html = WebModule.render("./source/modules/account/sections/vote.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
			} catch(Exception e) {
				prop.put("success", false);
				prop.put("error", "Error loading vote section");
				e.printStackTrace();
			}
			break;
		case "get-time":
			HashMap<Integer, Integer> times = new HashMap<>();
			for(int i = 0; i < 3; i++)
				times.put(i, VotingManager.getTime(account.getUsername(), i));
			prop.put("success", true);
			prop.put("times", gson.toJson(times));
			break;
		}
		return gson.toJson(prop);
	}

}
