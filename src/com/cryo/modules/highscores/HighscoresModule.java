package com.cryo.modules.highscores;

import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.cryo.modules.highscores.HSUtils.HSData;
import com.cryo.utils.Utilities;

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
		try {
			if(type == RequestType.GET) {
				model.put("hsusers", HSUtils.getList(30));
				model.put("skill_id", -1);
				return render("./source/modules/highscores/global/index.jade", model, request, response);
			} else if(type == RequestType.POST) {
				if(request.queryParams().contains("skill_id")) {
					int skill_id = Integer.parseInt(request.queryParams("skill_id"));
					model.put("skill_id", skill_id);
					model.put("skill_name", Utilities.SKILL_NAME[skill_id]);
					model.put("hsusers", HSUtils.getSkillList(skill_id));
					return render("./source/modules/highscores/global/skill-view.jade", model, request, response);
				}
				model.put("hsusers", HSUtils.getList(10));
				if(!request.queryParams().contains("display"))
					return render("./source/modules/highscores/mini/global-mini.jade", model, request, response);
				String name = request.queryParams("display");
				HSData data = new HSUtils().getHSData(name);
				model.put("hsdata", data);
				model.put("hsname", name);
				return render("./source/modules/highscores/mini/personal-mini.jade", model, request, response);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return Website.render404(request, response);
	}
	
}
