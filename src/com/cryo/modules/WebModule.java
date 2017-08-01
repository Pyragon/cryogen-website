package com.cryo.modules;

import java.io.IOException;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.account.AccountDAO;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.forums.ForumUser;
import com.cryo.modules.forums.ForumUtils;
import com.cryo.modules.highscores.HSUtils;
import com.cryo.utils.CookieManager;
import com.cryo.utils.DateUtils;
import com.cryo.utils.JadeIterator;
import com.cryo.utils.Utilities;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import lombok.Synchronized;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 7:16:12 PM
 */
public abstract class WebModule {
	
	protected Website website;
	
	public WebModule(Website website) {
		this.website = website;
	}
	
	public abstract Object decodeRequest(Request request, Response response, RequestType type);
	
	@Synchronized
	public String render(String file, HashMap<String, Object> model, Request request, Response response) {
		model.put("jIterator", new JadeIterator());
		model.put("hsutils", new HSUtils());
		model.put("utils", new Utilities());
		model.put("baseurl", "http://localhost/");
		model.put("formatter", new DateUtils());
		model.put("acutils", new AccountUtils());
		model.put("online", Utilities.getOnlinePlayers());
		model.put("shutdown", Website.SHUTDOWN_TIME);
		AccountDAO account = CookieManager.getAccount(request);
		model.put("loggedIn", account != null);
		if(account != null)
			model.put("user", account);
		model.put("isMobile", request.headers("User-Agent").toLowerCase().contains("Mobile"));
		try {
			String html = Jade4J.render(file, model);
			while(html.contains("$for-name=")) {
				String format = html.substring(html.indexOf("$for-name=")+10);
				format = format.substring(0, format.indexOf("$end"));
				AccountDAO acc = AccountUtils.getAccount(format);
				String name = Utilities.formatNameForDisplay(format);
				if(acc != null)
					name = AccountUtils.crownHTML(acc);
				html = html.replace("$for-name="+format+"$end", name);
			}
			while(html.contains("$forum-name=")) {
				String format = html.substring(html.indexOf("$forum-name=")+12);
				format = format.substring(0, format.indexOf("$end"));
				ForumUser user = ForumUtils.getUser(format);
				String name = format;
				if(user != null)
					name = ForumUtils.crownUser(user);
				html = html.replace("$forum-name="+format+"$end", name);
			}
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
	
	public String showLoginPage(String redirect, Request request, Response response) {
		if(CookieManager.isLoggedIn(request))
			return redirect("/", 0, request, response);
		HashMap<String, Object> model = new HashMap<>();
		model.put("redirect", redirect);
		return render("./source/modules/account/login.jade", model, request, response);
	}
	
	public String redirect(String redirect, Request request, Response response) {
		return redirect(redirect, 5, request, response);
	}
	
	public String redirect(String redirect, int time, Request request, Response response) {
		if(redirect == null || redirect == "")
			redirect = "/";
		if(time == -1) {
			response.redirect(redirect);
			return "";
		}
		HashMap<String, Object> model = new HashMap<>();
		model.put("redirect", redirect);
		model.put("time", time);
		return render("./source/modules/redirect.jade", model, request, response);
	}
	
}
