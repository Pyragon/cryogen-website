package com.cryo.modules.account;

import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoint;
import com.cryo.modules.index.Index;
import spark.Request;
import spark.Response;

import java.util.HashMap;

import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Recovery {

    @SPAEndpoint("/forgot")
    public static String renderRecoveryPage(Request request, Response response) {
        if(AccountUtils.getAccount(request) != null)
            return Index.renderIndexPage(request, response);
        String username = request.queryParamOrDefault("username", "");
        HashMap<String, Object> model = new HashMap<>();
        model.put("username", username);
        return renderPage("account/recovery/index", model, request, response);
    }

}
