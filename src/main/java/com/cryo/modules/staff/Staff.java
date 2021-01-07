package com.cryo.modules.staff;

import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoints;
import spark.Request;
import spark.Response;

import java.util.HashMap;

import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Staff {

    @SPAEndpoints("/staff, /staff/player-reports, /staff/bug-reports, /staff/appeals, /staff/recoveries")
    public static String renderSupportPage(String endpoint, Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        String section = "player-reports";
        if(!endpoint.equals("/staff"))
            section = endpoint.replace("/staff/", "");
        model.put("active", section);
        return renderPage("staff/index", model, endpoint, request, response);
    }
}
