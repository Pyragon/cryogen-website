package com.cryo.modules.live;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 8, 2017 at 10:45:02 AM
 */
public class LiveModule extends WebModule {
	
	public static String PATH = "/live";

	public LiveModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		HashMap<String, Object> model = new HashMap<>();
		return render("./source/modules/live/index.jade", model, request, response);
	}
	
}
