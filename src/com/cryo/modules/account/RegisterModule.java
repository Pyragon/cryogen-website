package com.cryo.modules.account;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.PreviousConnection;
import com.cryo.db.impl.AccountConnection;
import com.cryo.db.impl.DisplayConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.CookieManager;
import com.cryo.utils.DateUtils;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

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
				HttpResponse<JsonNode> node = body.asJson();
				JSONObject object = node.getBody().getObject();
				if(object == null) return error("Error loading recaptcha response.");
				if(!object.has("success") || !object.has("challenge_ts")) return error("Error loading recaptcha response.");
				boolean success = object.getBoolean("success");
				if(!success) return error("You failed the recaptcha! Please try again later.");
				Calendar cal = javax.xml.bind.DatatypeConverter.parseDateTime(object.getString("challenge_ts"));
				if(DateUtils.getDateDiff(cal.getTime(), new Date(), TimeUnit.MINUTES) > 10) return error("Token has expired. Please refresh the page and try again.");
			} catch (UnirestException e) {
				e.printStackTrace();
				return error(e.getMessage());
			}
			//Check recaptcha result
			Object[] data = DisplayConnection.connection().handleRequest("name-exists", username, username);
			if(data == null)
				return error("Error retrieving display name details");
			if((boolean) data[0])
				return error("Username is already in use.");
			String valid = Utilities.isValidDisplay(username);
			if(valid != null && !valid.equals(""))
				return error(valid);
			GlobalConnection connection = GlobalConnection.connection();
			data = connection.handleRequest("register", username, password);
			if(data == null)
				return error("Error registering.");
			String salt = (String) data[0];
			String hash = (String) data[1];
			data = connection.handleRequest("get-account", username);
			if(data == null)
				return redirect("/login", 0, request, response);
			Account account = (Account) data[0];
			PreviousConnection.connection().handleRequest("add-prev-hash", salt, hash);
			String sess_id = (String) AccountConnection.connection().handleRequest("add-sess", username)[0];
			response.cookie("cryo-sess", sess_id);
			return json(true, redirect("/register?success", 0, request, response));
		}
		HashMap<String, Object> model = new HashMap<>();
		model.put("success", request.queryParams().contains("success"));
		model.put("siteKey", "6LdcjYgUAAAAAA6w1_a2z8I5tsLFpGaYSiruNI62");
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
