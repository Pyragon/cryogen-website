package com.cryo.modules.accounts;

import com.cryo.entities.Endpoint;
import com.cryo.entities.EndpointSubscriber;
import com.cryo.entities.SPAEndpoint;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.Session;
import com.cryo.modules.index.IndexModule;
import com.cryo.utils.BCrypt;
import com.cryo.utils.SessionIDGenerator;
import com.cryo.utils.Utilities;
import spark.Request;
import spark.Response;

import java.sql.Timestamp;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.error;
import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class LoginModule {

    @Endpoint(method = "GET", endpoint = "/login")
    public static String loadLoginPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account != null)
            return IndexModule.load(request, response);
        return renderPage("account/login", null, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/login")
    public static String login(Request request, Response response) {
        if(AccountUtils.getAccount(request) != null)
            return error("You are already logged in. Please refresh the page.");
        String username = request.queryParams("username").toLowerCase();
        String password = request.queryParams("password");
        String rememberS = request.queryParams("remember");
        String redirect = request.queryParamOrDefault("redirect", "/");
        boolean remember = rememberS.equals("on");
        Account account = getConnection("cryogen_global").selectClass("player_data", "username=?", Account.class, username);
        if(account == null)
            return error("Invalid username or password.");
        String hashed = BCrypt.hashPassword(password, account.getSalt());
        if(!hashed.equals(account.getPassword()))
            return error("Invalid username or password.");
        String sessionId = SessionIDGenerator.getInstance().getSessionId();
        long expiry = (remember ? (1000 * 60 * 60 * 24 * 60) : (1000 * 60 * 60 * 24 * 1)) + System.currentTimeMillis();
        Session session = new Session(-1, username, sessionId, new Timestamp(expiry));
        getConnection("cryogen_accounts").insert("sessions", session.data());
        request.session().attribute("cryo_sess", sessionId);
        return Utilities.redirect(redirect, request, response);
    }

    @SPAEndpoint("/logout")
    public static String logout(Request request, Response response) {
        if(AccountUtils.getAccount(request) == null)
            return IndexModule.load(request, response);
        request.session().removeAttribute("cryo_sess");
        String redirect = request.queryParamOrDefault("redirect", "/");
        return Utilities.redirect(redirect, request, response);
    }
}
