package com.cryo.modules.staff;

import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoints;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.cryo.utils.Utilities;
import spark.Request;
import spark.Response;

import java.util.HashMap;

import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Staff {

    @SPAEndpoints("/staff, /staff/player-reports, /staff/bug-reports, /staff/appeals, /staff/punishments, /staff/recoveries")
    public static String renderStaffPage(String endpoint, Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/staff/appeals", request, response);
        if(account.getRights() < 1) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        String section = "player-reports";
        if(!endpoint.equals("/staff"))
            section = endpoint.replace("/staff/", "");
        model.put("active", section);
        return renderPage("staff/index", model, endpoint, request, response);
    }
}
