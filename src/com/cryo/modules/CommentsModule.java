package com.cryo.modules;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.entities.CommentList;
import com.cryo.managers.CommentsManager;
import com.cryo.modules.account.entities.Account;
import com.cryo.managers.CookieManager;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

import static spark.Spark.*;

import java.util.Properties;

public class CommentsModule extends WebModule {

	public CommentsModule(Website website) {
		super(website);
	}
	
	public static void registerEndpoints(Website web) {
		get("/comments", (req, res) -> {
			return new CommentsModule(web).decodeRequest(req, res, RequestType.GET);
		});
		post("/comments", (req, res) -> {
			return new CommentsModule(web).decodeRequest(req, res, RequestType.POST);
		});
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		String action = request.queryParams("action");
		Properties prop = new Properties();
		Gson gson = Website.getGson();
		if(!CookieManager.isLoggedIn(request)) return gson.toJson(prop);
		Account account = CookieManager.getAccount(request);
		if(account == null) return gson.toJson(prop);
		CommentsManager manager = Website.instance().getCommentsManager();
		switch(action) {
		case "submit":
			String comment = request.queryParams("comment");
			int id = Integer.parseInt(request.queryParams("id"));
			if(comment == null || comment.length() < 5) {
				prop.put("success", false);
				prop.put("error", "Comment must have a length of at least 5 characters.");
				break;
			}
			CommentList list = manager.getCommentList(id);
			if(list == null) {
				prop.put("success", false);
				prop.put("error", "Error loading comment list.");
				break;
			}
			if(account.getRights() < list.getRightsReq() || (!account.getUsername().equals(list.getCreator()) && account.getRights() == 0)) {
				prop.put("success", false);
				prop.put("error", "You do not have sufficient permission to comment on this.");
				break;
			}
			manager.addComment(account.getUsername(), comment, id);
			String html = manager.getComments(id, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		case "remove":
			id = Integer.parseInt(request.queryParams("id"));
			int listId = Integer.parseInt(request.queryParams("listid"));
			if(account.getRights() < 2) {
				prop.put("success", false);
				prop.put("error", "Only Admins can remove comments.");
				break;
			}
			manager.removeComment(listId, id);
			html = manager.getComments(listId, request, response);
			prop.put("success", true);
			prop.put("html", html);
			break;
		}
		return gson.toJson(prop);
	}

}
