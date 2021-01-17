package com.cryo.modules.account;

import com.cryo.Website;
import com.cryo.entities.accounts.Session;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoints;
import com.cryo.modules.index.Index;
import com.cryo.utils.BCrypt;
import com.cryo.utils.Utilities;
import nl.basjes.parse.useragent.UserAgent;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class Account {

    @SPAEndpoints("/account, /account/overview, /account/vote, /account/packages, /account/shop")
    public static String renderAccountPage(String endpoint, Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        String section = "overview";
        if(!endpoint.equals("/account"))
            section = endpoint.replace("/account/", "");
        model.put("active", section);
        return renderPage("account/index", model, endpoint, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/account/logins")
    public static String viewLogins(Request request, Response response) {
        com.cryo.entities.accounts.Account account = AccountUtils.getAccount(request);
        if(account == null)
            return Login.renderLoginPage("/account/overview", request, response);
        String sessionId = request.cookie("cryo_sess");
        if(sessionId == null)
            sessionId = request.session().attribute("cryo_sess");
        final String finalSess = sessionId;
        HashMap<String, Object> model = new HashMap<>();
        List<Session> sessions = getConnection("cryogen_accounts").selectList("sessions", "username=?", Session.class, account.getUsername());
        sessions.forEach(s -> {
            if(s.getSessionId().equals(finalSess))
                s.setCurrent(true);
        });
        model.put("logins", sessions);
        return renderPage("utils/account/logins", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/account/revoke")
    public static String revokeLogins(Request request, Response response) {
        com.cryo.entities.accounts.Account account = AccountUtils.getAccount(request);
        if(account == null)
            return Login.renderLoginPage("/account/overview", request, response);
        String sessionId = request.cookie("cryo_sess");
        if(sessionId == null)
            sessionId = request.session().attribute("cryo_sess");
        if(request.queryParams().contains("session")) {
            String toRemove = request.queryParams("session");
            Session session = getConnection("cryogen_accounts").selectClass("sessions", "session_id=?", Session.class, toRemove);
            if(!session.getUsername().equals(account.getUsername()))
                return error("Unable to find that session. Please refresh the page and try again.");
            if(toRemove.equals(sessionId))
                return error("You cannot revoke your current login. Please relog, and then revoke the old login.");
            getConnection("cryogen_accounts").delete("sessions", "session_id=?", toRemove);
            return error("Your logins have been successfully revoked.");
        }
        getConnection("cryogen_accounts").delete("sessions", "username = ? AND session_id != ?", account.getUsername(), sessionId);
        return error("Your logins have been successfully revoked.");
    }

    @Endpoint(method = "POST", endpoint = "/account/password/reset")
    public static String resetPassword(Request request, Response response) {
        com.cryo.entities.accounts.Account account = AccountUtils.getAccount(request);
        if(account == null)
            return Index.renderIndexPage(request, response);
        boolean forced = Boolean.parseBoolean(request.queryParamOrDefault("forced", "false"));
        if(forced && !account.isPasswordResetRequired())
            return error("A password reset is not required for your account. Please refresh the page and try again.");
        else if(!forced) {
            String current = request.queryParams("current");
            String hash = BCrypt.hashPassword(current, account.getSalt());
            if(!hash.equals(account.getPassword()))
                return error("Invalid current password. Please try again.");
        }
        String password = request.queryParams("password");
        if(password.length() < 6 || password.length() > 20)
            return error("Password must be between 6 and 20 characters.");
        String hash = BCrypt.hashPassword(password, account.getSalt());
        getConnection("cryogen_global").set("player_data", "password=?", "username=?", hash, account.getUsername());
        return Utilities.redirect("/", "Password "+(forced ? "reset" : "change")+" successful.", null, null, request, response);
    }
}
