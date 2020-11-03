package com.cryo.modules;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.Misc;
import com.cryo.entities.forums.Post;
import com.cryo.entities.forums.Thread;
import com.cryo.managers.CookieManager;
import com.cryo.managers.NotificationManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.highscores.HSUtils;
import com.cryo.utils.*;
import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import lombok.Synchronized;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 7:16:12 PM
 */
public abstract class WebModule {
	
	protected Website website;

	public WebModule() {
		this.website = Website.instance();
	}
	
	public WebModule(Website website) {
		this.website = website;
	}

	public String[] getEndpoints() {
		return new String[]{};
	}

	public String decodeRequest(String endpoint, Request request, Response response) {
		return error("Error decoding request.");
	}
	
	public abstract Object decodeRequest(Request request, Response response, RequestType type);
	
	@Synchronized
	public static String render(String file, HashMap<String, Object> model, Request request, Response response) {
		model.put("jIterator", new JadeIterator());
		model.put("hsutils", new HSUtils());
		model.put("utils", new Utilities());
		model.put("baseurl", Website.getProperties().getProperty("path"));
		model.put("formatter", new DateUtils());
		model.put("acutils", new AccountUtils());
		model.put("shutdown", Website.SHUTDOWN_TIME);
		model.put("tools", new Tools());
		Account account = CookieManager.getAccount(request);
		model.put("loggedIn", account != null);
		if(account != null) {
			model.put("user", account);
			model.put("notifications", new NotificationManager(account));
		}
        //forum stats
        model.put("registeredUsers", GlobalConnection.connection().selectCount("player_data", null));
        model.put("onlineUsers", ForumConnection.connection().selectCount("account_statuses", "expiry > CURRENT_TIMESTAMP()"));
        Misc misc = GlobalConnection.connection().selectClass("misc_data", "name=?", Misc.class, "most_online");
        model.put("mostOnline", misc == null ? "N/A" : misc.asInt());

        Object[] data = (Object[]) Website.instance().getCachingManager().getData("forum-stats", account);
		if(data == null) {
			model.put("totalThreads", "N/A");
			model.put("totalPosts", "N/A");
			model.put("latestPosts", new ArrayList<Post>());
			model.put("latestThreads", new ArrayList<Thread>());
		} else {
			model.put("totalThreads", data[0]);
			model.put("totalPosts", data[1]);
			model.put("latestPosts", data[2]);
			model.put("latestThreads", data[3]);
		}

		model.put("isMobile", request.headers("User-Agent").toLowerCase().contains("mobile"));
		try {
			String html = Jade4J.render(file, model);
			while(html.contains("$for-name=")) {
				String format = html.substring(html.indexOf("$for-name=")+10);
				format = format.substring(0, format.indexOf("$end"));
				Account acc = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, format);
				String name = Utilities.formatNameForDisplay(format);
				if(acc != null)
					name = AccountUtils.crownHTML(acc);
				html = html.replace("$for-name="+format+"$end", name);
			}
//			while (html.contains("$forums-name=")) {
//				String format = html.substring(html.indexOf("$forums-name=") + 12);
//				format = format.substring(0, format.indexOf("$end"));
//				ForumUser user = ForumUtils.getUser(format);
//				String name = format;
//				if(user != null)
//					name = ForumUtils.crownUser(user);
//				html = html.replace("$forums-name=" + format + "$end", name);
//			}
			while(html.contains("$link=")) {
				String format = html.substring(html.indexOf("$link=")+6);
				format = format.substring(0, format.indexOf("$end"));
				String[] strings = format.split(":");
				String link = strings[0];
				String title = strings[1];
				String onc = strings[2];
				String realLink = "<a onclick=\""+onc+"; return false;\" href=\""+link+"\">"+title+"</a>";
				html = html.replace("$link="+link+":"+title+":"+onc+"$end", realLink);
			}
			return html;
		} catch (JadeCompilerException | IOException e) {
			e.printStackTrace();
		}
		return "ERROR";
	}
	
	public static String showLoginPage(String redirect, Request request, Response response) {
		if(CookieManager.isLoggedIn(request))
			return redirect("/", 0, request, response);
		HashMap<String, Object> model = new HashMap<>();
		model.put("redirect", redirect);
		return render("./source/modules/account/login.jade", model, request, response);
	}

	public static String error(String error) {
		Properties prop = new Properties();
		prop.put("success", false);
		prop.put("error", error);
		return Website.getGson().toJson(prop);
	}
	
	public static String redirect(String redirect, Request request, Response response) {
		return redirect(redirect, null, 5, null, request, response);
	}
	
	public static String redirect(String redirect, int time, Request request, Response response) {
		return redirect(redirect, null, time, null, request, response);
	}

	public static String redirect(String redirect, String extraInfo, int time, HashMap<String, Object> model, Request request, Response response) {
		if(model == null) model = new HashMap<>();
		if(redirect == null || redirect == "")
			redirect = "/";
		if(time == -1) {
			response.redirect(redirect);
			return "";
		}
		if (extraInfo != null)
			model.put("extra_info", extraInfo);
		model.put("redirect", redirect);
		model.put("time", time);
		String html = render("./source/modules/redirect.jade", model, request, response);
		if (request.requestMethod().equals("POST")) {
			Properties prop = new Properties();
			prop.put("success", false);
			prop.put("redirect", html);
			return Website.getGson().toJson(prop);
		}
		return html;
	}
	
}
