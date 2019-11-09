package com.cryo.modules.index;

import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.cache.CachedItem;
import com.cryo.modules.WebModule;
import com.cryo.utils.DateUtils;

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
		if(type != RequestType.GET)
			return Website.render404(request, response);
		HashMap<String, Object> model = new HashMap<>();
		model.put("formatter", new DateUtils());
		CachedItem cache = Website.instance().getCachingManager().get("hs-cache");
		if(cache == null)
			return Website.error("Error loading HS Cache.");
		model.put("hsusers", cache.getCachedData("mini-list"));
		model.put("redirect", "/");
		String html = null;
		try {
			html = render("./source/modules/index/index.jade", model, request, response);
		} catch(Exception e) {
			System.err.println(e);
			return error("Error loading.");
		}
		return html;
	}
	
	@RequiredArgsConstructor
	public static class PostDAO {
		
		private final @Getter String subject, message, username;
		
		private final @Getter long dateline;
		
	}
	
	public static class PostList {
		
		private final @Getter ArrayList<PostDAO> list;
		
		public PostList() {
			list = new ArrayList<>();
		}
		
		public void add(PostDAO thread) {
			list.add(thread);
		}
		
	}
	
}
