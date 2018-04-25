package com.cryo.modules.samsung;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;

import spark.Request;
import spark.Response;

public class SamsungTVModule extends WebModule {
	
	public static String PATH = "/tv";

	public SamsungTVModule(Website website) {
		super(website);
	}

	@Override
	public Object decodeRequest(Request request, Response response, RequestType type) {
		HashMap<String, Object> model = new HashMap<>();
		if(type == RequestType.GET)
			return render("./source/modules/samsung/index.jade", model, request, response);
		return null;
	}

}
