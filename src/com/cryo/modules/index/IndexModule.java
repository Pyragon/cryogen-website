package com.cryo.modules.index;

import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.ForumConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.highscores.HSUtils;
import com.cryo.utils.DateFormatter;

import de.neuland.jade4j.Jade4J;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: Mar 7, 2017 at 7:15:59 PM
 */
public class IndexModule extends WebModule {
	
	public IndexModule(Website website) {
		super(website);
	}
	
	public static String PATH = "/";
	
	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		if(type != RequestType.GET) {
			//Why are we posting to the index?
			return "";
		}
		ForumConnection connection = (ForumConnection) website.getConnectionManager().getConnection(Connection.FORUMS);
		PostList list = (PostList) connection.handleRequest("get-latest-threads")[0];
		HashMap<String, Object> model = new HashMap<>();
		model.put("postList", list);
		model.put("formatter", new DateFormatter());
		model.put("hsusers", HSUtils.getList(10));
		model.put("redirect", "/re2");
		return render("./source/modules/index/index.jade", model, request, response);
	}
	
	@RequiredArgsConstructor
	public static class LatestPost {
		
		private final @Getter String subject, message, username;
		
		private final @Getter long dateline;
		
	}
	
	public static class PostList {
		
		private final @Getter ArrayList<LatestPost> list;
		
		public PostList() {
			list = new ArrayList<>();
		}
		
		public void add(LatestPost thread) {
			list.add(thread);
		}
		
	}
	
}
