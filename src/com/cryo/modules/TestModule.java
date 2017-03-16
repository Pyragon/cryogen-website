package com.cryo.modules;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;

import lombok.val;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 14, 2017 at 11:26:19 PM
 */
public class TestModule extends WebModule {
	
	public TestModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		val model = new HashMap<String, Object>();
		switch(type) {
			case GET:
				break;
			case POST:
				break;
		}
		return render("./source/modules/test.jade", model, request, response);
	}
	
}
