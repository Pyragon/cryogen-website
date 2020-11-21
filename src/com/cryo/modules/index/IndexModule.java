package com.cryo.modules.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.cache.CachedItem;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.Post;
import com.cryo.entities.forums.Thread;
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
		List<Post> posts = ForumConnection.connection()
				.selectList("threads", "archived=0 AND forum_id IN (1,2)", "ORDER BY added DESC LIMIT 4", Thread.class)
				.stream()
				.map(t -> t.getFirstPost())
				.collect(Collectors.toList());
		model.put("newsPosts", posts);
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
	
}
