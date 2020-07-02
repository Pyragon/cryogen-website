package com.cryo.modules.forums.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.entities.forums.ForumAdminSection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.managers.CookieManager;
import com.cryo.utils.Utilities;

import spark.Request;
import spark.Response;

public class ForumAdminModule extends WebModule {

    private static HashMap<String, ForumAdminSection> sections;

    static {
        loadSections();
    }

    @Override
    public String[] getEndpoints() {
        return new String[] {
            "GET", "/forums/admin",
            "GET", "/forums/admin/:section",
            "POST", "/forums/admin/:section",
            "POST", "/forums/admin/:section/:action",
        };
    }

    @Override
    public String decodeRequest(String endpoint, Request request, Response response) {
        Properties prop = new Properties();
        HashMap<String, Object> model = new HashMap<>();
        if(!CookieManager.isLoggedIn(request))
            return WebModule.showLoginPage("/forums/admin", request, response);
        Account account = CookieManager.getAccount(request);
        if(account.getRights() < 2)
            return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
        switch(endpoint) {
            case "/forums/admin":
                ForumAdminSection section = sections.get("overview");
                if (section == null)
                    return Website.render404(request, response);
                System.out.println("hi");
                model.put("section", section);
                return WebModule.render("./source/modules/forums/admin/admin.jade", model, request, response);
            case "/forums/admin/:section":
                String name = request.params(":section");
                if (name == null || (section = sections.get(name)) == null)
                    return request.requestMethod().equals("GET") ? Website.render404(request, response) : error("Section not found!");
                if(request.requestMethod().equals("GET")) {
                    model.put("section", section);
                    return WebModule.render("./source/modules/forums/admin/admin.jade", model, request, response);
                }
                String action = request.queryParams("action");
                if(action == null) return error("Invalid action!");
                return section.decode(action, request, response);
            case "/forums/admin/:section/:action":
                name = request.params(":section");
                action = request.params(":action");
                if (name == null || (section = sections.get(name)) == null)
                    return error("Section not found!");
                if(action == null) return error("Invalid action!");
                return section.decode(action, request, response);
        }
        return Website.getGson().toJson(prop);
    }

    @Override
    public Object decodeRequest(Request request, Response response, RequestType type) {
        
        return null;
    }

    private static void loadSections() {
        try {
            sections = new HashMap<>();
            for (Class<?> c : Utilities.getClasses("com.cryo.modules.forums.admin.impl")) {
                if (c.isAnonymousClass())
                    continue;
                if (!ForumAdminSection.class.isAssignableFrom(c))
                    continue;
                Object o = c.newInstance();
                if (!(o instanceof ForumAdminSection))
                    continue;
                ForumAdminSection section = (ForumAdminSection) o;
                sections.put(section.getName(), section);
            }
        } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}