package com.cryo.modules.account.sections;

import com.cryo.entities.Endpoint;
import com.cryo.entities.EndpointSubscriber;
import com.cryo.entities.accounts.Account;
import com.cryo.modules.account.AccountUtils;
import spark.Request;
import spark.Response;

import java.util.HashMap;

import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Shop {

    @Endpoint(method = "POST", endpoint = "/account/shop/load")
    public static String renderShopPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        assert account != null;
        HashMap<String, Object> model = new HashMap<>();
        return renderPage("account/sections/shop", model, request, response);
    }

}
