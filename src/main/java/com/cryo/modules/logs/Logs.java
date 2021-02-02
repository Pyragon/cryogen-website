package com.cryo.modules.logs;

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
public class Logs {

    @SPAEndpoints("/logs, /logs/login, /logs/command, /logs/trade, /logs/items, /logs/drop, /logs/death, /logs/pickup, /logs/pvp, /logs/duel, /logs/dice, /logs/pos, /logs/shop")
    public static String renderLogsPage(String endpoint, Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/logs", request, response);
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        String section = "login";
        if(!endpoint.equals("/logs"))
            section = endpoint.replace("/logs/", "");
        model.put("active", section);
        return renderPage("logs/index", model, endpoint, request, response);
    }
}
