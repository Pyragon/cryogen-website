package com.cryo.modules.account;

import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.VotingConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.vote.VotingManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

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
	
	public void decodeVoteGet(Request request, Response response) {
		if(!request.queryParams().contains("action") || !request.queryParams().contains("secret"))
			return;
		String secret = request.queryParams("secret");
		if(!secret.equals("yk1rH9w06360sXN"))
			return;
		String username = request.queryParams("uid");
		switch(request.queryParams("action")) {
			case "rune-server":
				System.out.println("received vote for "+username);
				break;
		}
	}
	
	public String decodeVotePost(Request request, Response response) {
		if(request.session().attribute("cryo-user") == null)
			return showLoginPage("/account?section=vote", request, response);
		HashMap<String, Object> model = new HashMap<>();
		if(!request.queryParams().contains("action"))
			return Website.render404(request, response);
		String action = request.queryParams("action");
		switch(action) {
			case "refresh":
				Properties prop = new Properties();
				VotingManager manager = new VotingManager(request.session().attribute("cryo-user"));
				model.put("voteManager", manager);
				prop.put("authlist", render("./source/modules/account/sections/vote/auth-list.jade", model, request, response));
				for(int i = 0; i < 3; i++) {
					prop.put("site"+i, manager.getTime(i));
				}
				return new Gson().toJson(prop);
			case "exchange":
				String auid = request.queryParams("id");
				Object[] data = VotingConnection.connection().handleRequest("remove-auth", auid);
				if(data == null)
					return "error";
				//TODO - send message to server
				return "success";
			case "remove":
				auid = request.queryParams("id");
				VotingConnection.connection().handleRequest("remove-auth", auid);
				return "success";
		}
		return Website.render404(request, response);
	}
	
	public static void logAuthEvent() {
		//TODO - LOG EVERYTHING THAT HAPPENS TO AN AUTH
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		if(request.session().attribute("cryo-user") == null)
			return showLoginPage("/account?"+request.queryString(), request, response);
			HashMap<String, Object> model = new HashMap<>();
		if(type == RequestType.GET) {
			if(request.queryParams().contains("section"))
				model.put("section", request.queryParams("section"));
			String username = request.session().attribute("cryo-user");
			model.put("voteManager", new VotingManager(username));
			Object[] data = DisplayConnection.connection().handleRequest("get-time", username);
			int seconds = 0;
			if(data != null)
				seconds = (int) data[0];
			model.put("seconds", seconds);
			return render("./source/modules/account/index.jade", model, request, response);
		} else if(type == RequestType.POST) {
			String action = request.queryParams("action");
			if(action == null || action == "")
				return Website.render404(request, response);
			switch(action) {
				case "check-display":
					String name = request.queryParams("name");
					if(name == null || name == "")
						return "ERROR";
					Object[] data = DisplayConnection.connection().handleRequest("name-exists", name);
					if(data == null)
						return "ERROR";
					return Boolean.toString((boolean) data[0]);
				case "change-display":
					name = request.queryParams("name");
					String username = request.session().attribute("cryo-user");
					Account account = AccountUtils.getAccount(username);
					String display = AccountUtils.getDisplayName(account);
					if(name == null || name == "")
						return "Error changing Display Name. Please contact an admin.";
					DisplayConnection.connection().handleRequest("change-display", username, display, Utilities.formatName(name));
					data = DisplayConnection.connection().handleRequest("get-time", username);
					int seconds = (int) data[0];
					Properties prop = new Properties();
					prop.put("seconds", seconds);
					prop.put("success", "true");
					String html = AccountUtils.crownHTML(account);
					prop.put("displayname", html);
					return new Gson().toJson(prop);
			}
			return "";
		}
		return Website.render404(request, response);
	}
	
}
