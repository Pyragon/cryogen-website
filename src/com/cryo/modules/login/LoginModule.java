package com.cryo.modules.login;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.cookies.CookieManager;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.Account;

import static com.cryo.utils.Utilities.*;

import java.util.HashMap;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 8, 2017 at 9:44:44 AM
 */
public class LoginModule extends WebModule {
	
	public static String PATH = "/login";
	
	public LoginModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		if(type == RequestType.GET) {
			if(CookieManager.isLoggedIn(request))
				return redirect("/", 0, request, response);
			HashMap<String, Object> model = new HashMap<String, Object>();
			if(request.queryParams().contains("redirect"))
				model.put("redirect", request.queryParams("redirect"));
			return render("./source/modules/account/login.jade", model, request, response);
		} else if(type == RequestType.POST) {
			String username = request.queryParams("username");
			String password = request.queryParams("password");
			boolean isMini = request.queryParams("mini-login") != null && request.queryParams("mini-login").equals("true");
			GlobalConnection connection = (GlobalConnection) Website.instance().getConnectionManager().getConnection(Connection.GLOBAL);
			Object[] data = connection.handleRequest("compare", username, password);
			boolean success = data == null ? false : (boolean) data[0];
			if(success) {
				data = connection.handleRequest("get-account", username);
				if(data == null)
					return redirect("/login", 0, request, response);
				Account account = (Account) data[0];
				String sess_id = (String) connection.handleRequest("get-sess-id", account)[0];
				response.cookie("cryo-sess", sess_id, 604_800);
				return redirect(request.queryParams("redirect"), request, response);
			}
			if(isMini)
				return "failed";
			return redirect("/login", 0, request, response);
		}
		return Website.render404(request, response);
	}
	
}
