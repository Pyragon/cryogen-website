package com.cryo.modules.account.support;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.utils.DateFormatter;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 20, 2017 at 12:24:01 PM
 */
public class AccountSupportModule extends WebModule {
	
	public static String PATH = "/support";

	public AccountSupportModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		if(!request.session().attributes().contains("cryo-user")) {
			String string = "";
			if(request.queryString() != null)
				string = "?"+request.queryString();
			return showLoginPage("/support"+string, request, response);
		} 
		HashMap<String, Object> model = new HashMap<>();
		model.put("punishUtils", new PunishUtils());
		model.put("formatter", new DateFormatter());
		if(type == RequestType.GET) {
			if(request.queryParams().contains("section"))
				model.put("section", request.queryParams("section"));
			return render("./source/modules/support/index.jade", model, request, response);
		}
		String module = "overview";
		if(request.queryParams().contains("mod"))
			module = request.queryParams("mod");
		switch(module) {
			case "report_player":
				return ReportPlayerModule.decodeRequest(this, request, response);
			case "report_bug":
				return ReportBugModule.decodeRequest(this, request, response);
		}
		return null;
	}
	
}
