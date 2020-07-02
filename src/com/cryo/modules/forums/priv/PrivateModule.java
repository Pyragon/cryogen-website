package com.cryo.modules.forums.priv;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.entities.forums.PrivateSection;
import com.cryo.modules.WebModule;
import com.cryo.managers.CookieManager;
import com.cryo.utils.Utilities;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import static spark.Spark.get;
import static spark.Spark.post;

public class PrivateModule extends WebModule {

    private HashMap<String, PrivateSection> sections;

    public PrivateModule() {
        loadSections();
    }

    public String[] getEndpoints() {
        return new String[] {
                "GET", "/forums/private",
                "GET", "/forums/private/:section",
                "POST", "/forums/private/:section",
                "POST", "/forums/private/:section/:action"
        };
    }

    @Override
    public Object decodeRequest(Request request, Response response, RequestType type) {
        return null;
    }

    @Override
    public String decodeRequest(String endpoint, Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        Properties prop = new Properties();
        switch(endpoint) {
            case "/forums/private":
                if(!CookieManager.isLoggedIn(request))
                    return WebModule.showLoginPage("/forums/private/inbox", request, response);
                PrivateSection section = sections.get("inbox");
                if(section == null)
                    return Website.render404(request, response);
                model.put("section", section);
                return WebModule.render("./source/modules/forums/private/index.jade", model, request, response);
            case "/forums/private/:section":
            case "/forums/private/:section/:action":
                String name = request.params(":section");
                if(name == null || (section = sections.get(name)) == null)
                    return Website.render404(request, response);
                if(!CookieManager.isLoggedIn(request))
                    return WebModule.showLoginPage("/forums/private/"+name, request, response);
                if(request.requestMethod().equals("GET")) {
                    model.put("section", section);
                    return WebModule.render("./source/modules/forums/private/index.jade", model, request, response);
                }
                String action = request.queryParams("action");
                if(action == null) action = request.params(":action");
                if(action == null) return error("Invalid action. Please try again.");
                return section.decode(action, request, response);
        }
        return null;
    }

    private void loadSections() {
        try {
            sections = new HashMap<>();
            for(Class<?> c : Utilities.getClasses("com.cryo.modules.forums.priv")) {
                if(c.isAnonymousClass()) continue;
                if(!PrivateSection.class.isAssignableFrom(c))
                    continue;
                Object o = c.newInstance();
                if(!(o instanceof PrivateSection))
                    continue;
                PrivateSection section = (PrivateSection) o;
                sections.put(section.getName(), section);
            }
        } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
