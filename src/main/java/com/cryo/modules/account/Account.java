package com.cryo.modules.account;

import com.cryo.Website;
import com.cryo.entities.Endpoint;
import com.cryo.entities.EndpointSubscriber;
import com.cryo.entities.SPAEndpoints;
import com.cryo.entities.discord.Discord;
import com.cryo.entities.discord.Verify;
import com.cryo.utils.DisplayNames;
import com.cryo.utils.Utilities;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Properties;

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
