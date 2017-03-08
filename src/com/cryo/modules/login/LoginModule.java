package com.cryo.modules.login;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.AccountConnection;
import com.cryo.modules.WebModule;
import static com.cryo.utils.Utilities.*;

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
		if(type == RequestType.POST) {
			String username = getRequestUsername(request);
			String password = getRequestPassword(request);
			boolean isMini = isMiniLogin(request);
			AccountConnection connection = (AccountConnection) Website.instance().getConnectionManager().getConnection(Connection.ACCOUNT);
			Object[] data = connection.handleRequest("compare", username, password);
			boolean success = data == null ? false : (boolean) data[0];
			if(success) {
				request.session().attribute("cryo-user", username);
				return redirect(getRequestRedirect(request), request, response);
			}
			System.out.println("hello");
			if(isMini)
				return "failed";
			return redirect("/login", 0, request, response);
		}
		return Website.render404(request, response);
	}
	
}
