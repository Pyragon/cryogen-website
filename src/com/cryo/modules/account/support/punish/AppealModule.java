package com.cryo.modules.account.support.punish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.db.impl.PunishmentConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.CookieManager;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 22, 2017 at 9:34:33 AM
 */
public class AppealModule {
	
	public static String decodeRequest(WebModule module, HashMap<String, Object> model, Request request, Response response) {
		Properties prop = new Properties();
		String action = request.queryParams("action");
		String username = CookieManager.getUsername(request);
		switch(action) {
			case "submit-com":
				String comment = request.queryParams("comment");
				int appealId = Integer.parseInt(request.queryParams("appealId"));
				if(comment == null || comment.equals("")) {
					prop.put("success", false);
					prop.put("error", "No comment entered.");
					break;
				}
				ArrayList<ACommentDAO> comments = new PunishUtils().getComments(appealId, 0);
				boolean both = true;
				if(comments.size() < 2)
					both = false;
				else {
					ACommentDAO[] last2 = new ACommentDAO[2];
					last2[0] = comments.get(0);
					last2[1] = comments.get(1);
					for(ACommentDAO last : last2) {
						if(last == null || !last.getUsername().equals(username)) {
							both = false;
							break;
						}
					}
				}
				Account acc = AccountUtils.getAccount(username);
				if(both && acc.getRights() == 0) {
					prop.put("success", false);
					prop.put("error", "Please wait for a staff member to respond before posting again.");
					break;
				}
				PunishmentConnection.connection().handleRequest("add-comment", username, appealId, 0, comment);
				prop.put("success", true);
				model.put("comments", new PunishUtils().getComments(appealId, 0));
				prop.put("html", module.render("./source/modules/utils/comments.jade", model, request, response));
				break;
			case "create-appeal":
				int id = Integer.parseInt(request.queryParams("id"));
				AppealDAO appeal = new PunishUtils().getAppealFromPunishment(id);
				if(appeal != null) {
					prop.put("success", false);
					prop.put("error", "Appeal already exists for this punishment!");
					break;
				}
				String title = request.queryParams("title");
				String detailed = request.queryParams("detailed");
				if(title.equals("") || detailed.equals("") || title.replaceAll(" ", "").equals("") || detailed.replaceAll(" ", "").equals("")) {
					prop.put("success", false);
					prop.put("error", "All fields must be filled out!");
					break;
				}
				if(title.length() > 20) {
					prop.put("success", false);
					prop.put("error", "Title cannot exceed 20 characters.");
					break;
				}
				if(detailed.length() > 1000) {
					prop.put("success", false);
					prop.put("error", "Detailed appeal cannot exceed 1k characters.");
					break;
				}
				PunishUtils.createAppeal(id, username, title, detailed);
				String html = module.render("./source/modules/support/sections/appeal/appeal_list.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
			case "click-appeal":
				id = Integer.parseInt(request.queryParams("id"));
				appealId = Integer.parseInt(request.queryParams("appealId"));
				model.put("pid", id);
				if(appealId == 0) {
					prop.put("title", "Below is the form to submit an appeal for your punishment. Be sure to read the checklist and press submit when you are ready.");
					prop.put("html", module.render("./source/modules/support/sections/appeal/create_appeal.jade", model, request, response));
					break;
				}
				appeal = new PunishUtils().getAppeal(appealId);
				if(appeal == null) {
					prop.put("title", "Below is the form to submit an appeal for your punishment. Be sure to read the checklist and press submit when you are ready.");
					prop.put("html", module.render("./source/modules/support/sections/appeal/create_appeal.jade", model, request, response));
					break;
				}
				PunishDAO punish = new PunishUtils().getPunishmentFromAppeal(appealId); //wasn't really needed.
				model.put("appeal", appeal);
				model.put("comments", new PunishUtils().getComments(appealId, 0));
				prop.put("title", "Now viewing appeal for punishment type: "+(punish.getType() == 0 ? "Mute" : "Ban"));
				prop.put("html", module.render("./source/modules/support/sections/appeal/view_appeal.jade", model, request, response));
				prop.put("display", "$for-name="+appeal.getUsername()+"$end");
				break;
			case "get-appeal-list":
				html = module.render("./source/modules/support/sections/appeal/appeal_list.jade", model, request, response);
				prop.put("success", true);
				prop.put("html", html);
				break;
		}
		return new Gson().toJson(prop);
	}
	
}
