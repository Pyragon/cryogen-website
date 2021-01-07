package com.cryo.modules.account.support;

import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoints;
import spark.Request;
import spark.Response;

import java.util.HashMap;

import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Support {

    @SPAEndpoints("/support, /support/player-reports, /support/bug-reports, /support/punishments, /support/appeals, /support/recoveries")
    public static String renderSupportPage(String endpoint, Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        String section = "player-reports";
        if(!endpoint.equals("/support"))
            section = endpoint.replace("/support/", "");
        model.put("active", section);
        return renderPage("account/support/index", model, endpoint, request, response);
    }
}
