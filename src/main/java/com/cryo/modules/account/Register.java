package com.cryo.modules.account;

import com.cryo.Website;
import com.cryo.entities.accounts.PreviousPassList;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoint;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.Session;
import com.cryo.modules.index.Index;
import com.cryo.utils.BCrypt;
import com.cryo.utils.DisplayNames;
import com.cryo.utils.SessionIDGenerator;
import com.cryo.utils.Utilities;
import com.mysql.cj.util.StringUtils;
import spark.Request;
import spark.Response;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class Register {

    @SPAEndpoint("/register")
    public static String renderRegistrationPage(Request request, Response response) {
        if(AccountUtils.getAccount(request) != null)
            return Index.renderIndexPage(request, response);
        String username = request.queryParamOrDefault("username", "");
        HashMap<String, Object> model = new HashMap<>();
        model.put("username", username);
        model.put("sitekey", Website.getProperties().getProperty("captcha_site_key"));
        return renderPage("account/register", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/register/complete")
    public static String completeRegistration(Request request, Response response) {
        if(AccountUtils.getAccount(request) != null)
            return Index.renderIndexPage(request, response);
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        String gresponse = request.queryParams("response");
        String visitorId = request.queryParams("visitorId");
        if(StringUtils.isNullOrEmpty(visitorId))
            return error("Invalid visitor id!");
        if(StringUtils.isNullOrEmpty(username) || username.length() < 3 || username.length() > 12)
            return error("Username must be between 3 and 12 characters.");
        if(StringUtils.isNullOrEmpty(password) || password.length() < 6 || password.length() > 20)
            return error("Password must be between 6 and 20 characters.");
        String result = Utilities.checkCaptchaResult(gresponse);
        if(result != null)
            return result;
        if(!DisplayNames.nameExists(username))
            return error("Username is already taken. Please try another one.");
        String salt = BCrypt.generate_salt();
        String hash = BCrypt.hashPassword(password, salt);
        String sessionId = SessionIDGenerator.getInstance().getSessionId();
        Account account = new Account(-1, username, hash, salt, 0, 0, null, "", null, null, -1, "", request.ip(), false, null, null);
        long expiry = (1000 * 60 * 60 * 24) + System.currentTimeMillis();
        Session session = new Session(-1, username, sessionId, request.userAgent(), visitorId, new Timestamp(expiry), null);
        ArrayList<String> list = new ArrayList<>();
        list.add(hash);
        PreviousPassList previous = new PreviousPassList(-1, username, list, null, null);
        getConnection("cryogen_previous").insert("passwords", previous.data());
        getConnection("cryogen_global").insert("player_data", account.data());
        getConnection("cryogen_accounts").insert("sessions", session.data());
        request.session().attribute("cryo_sess", sessionId);
        return redirect("/", null, null, "Account successfully created. Redirecting you to the home page!", request, response);
    }
}
