package com.cryo.modules.forums.admin.impl;

import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.entities.forums.ForumAdminSection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.managers.CookieManager;

import spark.Request;
import spark.Response;

public class OverviewSection implements ForumAdminSection {

    @Override
    public String getName() {
        return "overview";
    }

    @Override
    public String decode(String action, Request request, Response response) {
        Properties prop = new Properties();
        HashMap<String, Object> model = new HashMap<>();
        Account account = CookieManager.getAccount(request);
        System.out.println("here");
        switch(action) {
            case "load":
                String html = null;
                try {
                    html = WebModule.render("./source/modules/forums/admin/overview.jade", model, request,
                            response);
                } catch (Exception e) {
                    e.printStackTrace();
                    prop.put("success", false);
                    prop.put("error", e.getMessage());
                    break;
                }
                if (html == null) {
                    prop.put("success", false);
                    prop.put("error", "Unable to load section.");
                    break;
                }
                prop.put("success", true);
                prop.put("html", html);
                break;
        }
        return Website.getGson().toJson(prop);
    }

}