package com.cryo.modules.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.cookies.CookieManager;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.EmailConnection;
import com.cryo.db.impl.VotingConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.shop.ShopItem;
import com.cryo.modules.account.shop.ShopManager;
import com.cryo.modules.account.vote.VotingManager;
import com.cryo.utils.EmailUtils;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
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
		if(!CookieManager.isLoggedIn(request))
			return showLoginPage("/account?section=vote", request, response);
		HashMap<String, Object> model = new HashMap<>();
		if(!request.queryParams().contains("action"))
			return Website.render404(request, response);
		String action = request.queryParams("action");
		String username = CookieManager.getUsername(request);
		switch(action) {
			case "refresh":
				Properties prop = new Properties();
				VotingManager manager = new VotingManager(username);
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
		if(!CookieManager.isLoggedIn(request))
			return showLoginPage("/account?"+(request.queryString() == null ? "" : request.queryString()), request, response);
		String username = CookieManager.getUsername(request);
		HashMap<String, Object> model = new HashMap<>();
		if(type == RequestType.GET) {
			if(request.queryParams().contains("section"))
				model.put("section", request.queryParams("section"));
			model.put("shopItems", ShopManager.cached);
			model.put("shopManager", new ShopManager());
			if(request.queryParams().contains("action")) {
				String action = request.queryParams("action");
				switch(action) {
					case "verify":
						String random = request.queryParams("id");
						Object[] data = EmailConnection.connection().handleRequest("verify", random);
						model.put("success", data != null);
						return render("./source/modules/account/sections/overview/email-verify.jade", model, request, response);
					default: return Website.render404(request, response);
				}
			}
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
				case "get-checkout-conf":
				case "chg-quant":
				case "get-cart":
					return ShopManager.processRequest(action, request, response, type);
				case "check-display":
					String name = request.queryParams("name");
					if(name == null || name == "")
						return "ERROR";
					Object[] data = DisplayConnection.connection().handleRequest("name-exists", name);
					if(data == null)
						return "ERROR";
					return Boolean.toString((boolean) data[0]);
				case "get-email":
					data = EmailConnection.connection().handleRequest("get-email", username);
					if(data == null)
						return "";
					return (String) data[0];
				case "check-email":
					String email = request.queryParams("email");
					if(email == null || email == "")
						return "false";
					boolean match = email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
							+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
					return Boolean.toString(match);
				case "change-email":
					email = request.queryParams("email");
					String pass = request.queryParams("pass");
					Properties prop = new Properties();
					if(StringUtils.isNullOrEmpty(email) || StringUtils.isNullOrEmpty(pass)) {
						prop.put("result", false);
						prop.put("error", "Invalid email or password.");
						return new Gson().toJson(prop);
					}
					data = GlobalConnection.connection().handleRequest("compare", username, pass);
					if(data == null || !((boolean) data[0])) {
						prop.put("result", false);
						prop.put("error", "Invalid password.");
						return new Gson().toJson(prop);
					}
					match = email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
							+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
					if(!match) {
						prop.put("result", false);
						prop.put("error", "Invalid email.");
						return new Gson().toJson(prop);
					}
					prop.put("result", true);
					EmailUtils.sendVerificationEmail(username, email);
					return new Gson().toJson(prop);
				case "change-pass":
					String new_pass = request.queryParams("pass");
					String cur_pass = request.queryParams("cur");
					data = GlobalConnection.connection().handleRequest("change-pass", username, new_pass, cur_pass);
					boolean success = (boolean) data[0];
					prop = new Properties();
					prop.put("result", success);
					if(!success)
						prop.put("error", (String) data[1]);
					return new Gson().toJson(prop);
				case "change-display":
					name = request.queryParams("name");
					Account account = AccountUtils.getAccount(username);
					String display = AccountUtils.getDisplayName(account);
					if(name == null || name == "")
						return "Error changing Display Name. Please contact an admin.";
					DisplayConnection.connection().handleRequest("change-display", username, display, Utilities.formatName(name));
					data = DisplayConnection.connection().handleRequest("get-time", username);
					int seconds = (int) data[0];
					prop = new Properties();
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
