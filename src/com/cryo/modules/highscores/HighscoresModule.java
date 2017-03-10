package com.cryo.modules.highscores;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.cryo.modules.highscores.HSUtils.HSData;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 09, 2017 at 8:44:07 PM
 */
public class HighscoresModule extends WebModule {
	
	public static String PATH = "/highscores";
	
	public HighscoresModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		HashMap<String, Object> model = new HashMap<>();
		model.put("hsusers", HSUtils.getMiniList());
		if(type == RequestType.POST) {
			if(!request.queryParams().contains("display"))
				return render("./source/modules/highscores/global-mini.jade", model, request, response);
			String name = request.queryParams("display");
			model.put("hsname", name);
			return render("./source/modules/highscores/personal-mini.jade", model, request, response);
		}
		return Website.render404(request, response);
	}
	
}
