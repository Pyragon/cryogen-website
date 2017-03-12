package com.cryo.modules.login;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.AccountConnection;
import com.cryo.modules.WebModule;
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
			if(request.session().attributes().contains("cryo-user"))
				return redirect("/", 0, request, response);
			HashMap<String, Object> model = new HashMap<String, Object>();
			if(request.queryParams().contains("redirect"))
				model.put("redirect", request.queryParams("redirect"));
			return render("./source/modules/account/login.jade", model, request, response);
		} else if(type == RequestType.POST) {
			String username = request.queryParams("username");
			String password = request.queryParams("password");
			boolean isMini = request.queryParams("mini-login") != null && request.queryParams("mini-login").equals("true");
			AccountConnection connection = (AccountConnection) Website.instance().getConnectionManager().getConnection(Connection.ACCOUNT);
			Object[] data = connection.handleRequest("compare", username, password);
			boolean success = data == null ? false : (boolean) data[0];
			if(success) {
				request.session().attribute("cryo-user", username);
				return redirect(request.queryParams("redirect"), request, response);
			}
			if(isMini)
				return "failed";
			return redirect("/login", 0, request, response);
		}
		return Website.render404(request, response);
	}
	
}
