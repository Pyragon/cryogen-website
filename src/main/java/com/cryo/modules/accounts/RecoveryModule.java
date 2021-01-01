package com.cryo.modules.accounts;

import com.cryo.entities.EndpointSubscriber;
import com.cryo.modules.index.IndexModule;
import spark.Request;
import spark.Response;

import java.util.HashMap;

import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class RecoveryModule {

    public static String loadRecoveryPage(Request request, Response response) {
        if(AccountUtils.getAccount(request) != null)
            return IndexModule.load(request, response);
        String username = request.queryParamOrDefault("username", "");
        HashMap<String, Object> model = new HashMap<>();
        model.put("username", username);
        return renderPage("account/recovery/index", model, request, response);
    }

}
