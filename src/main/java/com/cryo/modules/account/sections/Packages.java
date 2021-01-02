package com.cryo.modules.account.sections;

import com.cryo.entities.Endpoint;
import com.cryo.entities.EndpointSubscriber;
import spark.Request;
import spark.Response;

import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Packages {

    @Endpoint(method = "POST", endpoint = "/account/packages/load")
    public static String renderPackagesPage(Request request, Response response) {
        return renderPage("account/sections/packages", null, request, response);
    }
}
