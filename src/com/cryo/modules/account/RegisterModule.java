package com.cryo.modules.account;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.AccountConnection;
import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.PreviousConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.BCrypt;
import com.cryo.managers.CookieManager;
import com.cryo.utils.DateUtils;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import org.joda.time.DateTime;

import spark.Request;
import spark.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 09, 2017 at 2:02:38 AM
 */
public class RegisterModule extends WebModule {
	
	public static String PATH = "/register";
	
	public RegisterModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		HashMap<String, Object> model = new HashMap<>();
		if(CookieManager.isLoggedIn(request) && !request.queryParams().contains("success"))
			return redirect("/", 0, request, response);
		if(type == RequestType.POST) {
			String username = request.queryParams("username");
			username = Utilities.formatNameForProtocol(username);
			String password = request.queryParams("password");
			String passVerify = request.queryParams("passwordVerify");
			String recaptchaToken = request.queryParams("token");
			String error = "";
			if(Utilities.hasNullOrEmpty(username,password,passVerify,recaptchaToken))
				error = "All fields must be filled out.";
			else if(username.length() < 3 || username.length() > 12)
				error = "Usernames must be between 3 and 12 characters";
			else if(password.length() < 6 || password.length() > 20)
				error = "Passwords must be between 6 and 20 characters";
			else if(!password.equals(passVerify))
				error = "Passwords do not match.";
			if(!error.equals(""))
				return error(error);
			HttpRequestWithBody body = Unirest.post("https://www.google.com/recaptcha/api/siteverify?secret="+Website.getProperties().getProperty("recaptcha_secret")+"&response="+recaptchaToken);
			try {
				HashMap<String, Object> obj = Website.getGson().fromJson(body.asString().getBody(), HashMap.class);
				if (obj == null) return error("Error loading recaptcha response.");
				if (!obj.containsKey("success") || !obj.containsKey("challenge_ts"))
					return error("Error loading recaptcha response.");
				boolean success = (boolean) obj.get("success");
				if(!success) return error("You failed the recaptcha! Please try again later.");
                DateTime dt = new DateTime((String) obj.get("challenge_ts"));
				if(DateUtils.getDateDiff(dt.toDate(), new Date(), TimeUnit.MINUTES) > 10) return error("Token has expired. Please refresh the page and try again.");
			} catch (UnirestException e) {
				e.printStackTrace();
				return error(e.getMessage());
			}
			Object[] data = DisplayConnection.connection().handleRequest("name-exists", username, username);
			if(data == null)
				return error("Error retrieving display name details");
			if((boolean) data[0])
				return error("Username is already in use.");
			String valid = Utilities.isValidDisplay(username);
			if(valid != null && !valid.equals(""))
				return error(valid);
			String salt = BCrypt.generate_salt();
			String hash = BCrypt.hashPassword(password, salt);
			Account account = new Account(-1, username, hash, salt, 0, 0, null, 2, null, null);
			if (account == null) return error("Error registering.");
			int insertId = GlobalConnection.connection().insert("player_data", account.data());
			if (insertId == -1) return error("Error registering.");
			String display = Utilities.formatNameForDisplay(username);
			DisplayConnection.connection().insert("current_names", username, display);
			PreviousConnection.connection().handleRequest("add-prev-hash", salt, hash);
			String sess_id = (String) AccountConnection.connection().handleRequest("add-sess", username)[0];
			response.cookie("cryo-sess", sess_id);
			Properties prop = new Properties();
			prop.put("success", true);
			model.put("success", true);
			prop.put("html", render("./source/modules/account/register.jade", model, request, response));
			return new Gson().toJson(prop);
		}
		model.put("success", request.queryParams().contains("success"));
		model.put("siteKey", "6LfrBeQUAAAAAPLHbfoDE_Pqd5BIUGDum0PgInJq");
		return render("./source/modules/account/register.jade", model, request, response);
	}
	
	public String json(boolean success, String data) {
		Properties prop = new Properties();
		prop.put("success", success);
		if(success)
			prop.put("data", data);
		else
			prop.put("error", data);
		return new Gson().toJson(prop);
	}
	
}
