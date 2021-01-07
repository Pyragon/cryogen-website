package com.cryo.modules.account;

import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoints;
import spark.Request;
import spark.Response;

import java.util.HashMap;

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

    @Endpoint(method = "POST", endpoint = "/account/revoke")
    public static String revokeLogins(Request request, Response response) {
        com.cryo.entities.accounts.Account account = AccountUtils.getAccount(request);
        if(account == null)
            return Login.renderLoginPage("/account/overview", request, response);
        String sessionId = request.cookie("cryo_sess");
        if(sessionId == null)
            sessionId = request.session().attribute("cryo_sess");
        getConnection("cryogen_accounts").delete("sessions", "username = ? AND session_id != ?", account.getUsername(), sessionId);
        return error("Your logins have been successfully revoked.");
    }
}
