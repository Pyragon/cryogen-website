package com.cryo.modules.api;

import com.cryo.Website;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.server.APIConnection;
import com.cryo.utils.CookieManager;
import com.google.gson.Gson;
import com.mashape.unirest.http.JsonNode;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static spark.Spark.*;
import static com.cryo.Website.*;

public class APIModule {

    public static void registerEndpoints(Website website) {
        get("/api", (request, response) -> renderAPI("overview", request, response));
        get("/api/:section", (request, response) -> {
            String section = request.params(":section");
            if(section == null)
                section = "overview";
            if(!APISections.getSections().containsKey(section))
                section = "overview";
            return renderAPI(section, request, response);
        });
        get("/api/type/:type", (request, response) -> {
            String typeName = request.params(":type");
            if(typeName == null || !APITypes.getTypes().containsKey(typeName)) return error("Invalid type.");
            APITypes type = APITypes.getTypes().get(typeName);
            Properties prop = new Properties();
            prop.put("returns", type.getReturns());
            prop.put("name", type.getName());
            if(type.isAbstractt()) {
                List<Properties> children = APITypes.getTypes().values().stream()
                        .filter(t -> t.getParentClass() != null && t.getParentClass().equals(type.getName()))
                        .map(t -> new Properties() {{
                            put("name", t.getName());
                            put("returns", t.getReturns());
                        }}).collect(Collectors.toList());
                String html = WebModule.render("./source/modules/api/abstract-type-noty.pug", new HashMap<String, Object>() {{
                    put("returns", type.getReturns());
                    put("name", type.getName());
                    put("children", children);
                }}, request, response);
                prop.put("html", html);
            }
            prop.put("success", true);
            System.out.println(prop);
            return getGson().toJson(prop);
        });
        post("/api/:section", (request, response) -> {
           String sectionName = request.params(":section");
           if(sectionName == null || !APISections.getSections().containsKey(sectionName))
               return error("Unable to find section.");
           APISections section = APISections.getSections().get(sectionName);
           String html = WebModule.render("./source/modules/api/endpoint.pug", new HashMap<String, Object>() {{
               put("realName", sectionName);
               put("name", section.getName() != null ? section.getName() : formatName(sectionName));
               put("message", section.getMessages());
               put("endpoints", section.getEndpoints());
           }}, request, response);
           Properties prop = new Properties();
           prop.put("success", true);
           prop.put("html", html);
           return getGson().toJson(prop);
        });
        post("/api/:section/:id/viewTest", (request, response) -> {
            String sectionName = request.params(":section");
            if(sectionName == null || !APISections.getSections().containsKey(sectionName))
                return error("Unable to find section.");
            APISections section = APISections.getSections().get(sectionName);
            String idName = request.params(":id");
            if(!NumberUtils.isDigits(idName)) return error("Invalid id.");
            int id = Integer.parseInt(idName);
            APIEndpoints endpoint = section.getEndpoint(id);
            if(endpoint == null) return error("Invalid id.");
            HashMap<String, Object> model = new HashMap<>();
            model.put("endpoint", endpoint);
            Properties prop = new Properties();
            prop.put("success", true);
            try {
                prop.put("html", WebModule.render("./source/modules/api/test-noty.pug", model, request, response));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getGson().toJson(prop);
        });
        post("/api/:section/:id/test", (request, response) -> {
            String sectionName = request.params(":section");
            if(sectionName == null || !APISections.getSections().containsKey(sectionName))
                return error("Unable to find section.");
            APISections section = APISections.getSections().get(sectionName);
            String idName = request.params(":id");
            if(!NumberUtils.isDigits(idName)) return error("Invalid id.");
            int id = Integer.parseInt(idName);
            APIEndpoints endpoint = section.getEndpoint(id);
            if(endpoint == null) return error("Invalid id.");
            Properties prop = new Properties();
            for(String name : request.queryParams())
                prop.put(name, request.queryParams(name));
            Account account = CookieManager.getAccount(request);
            APIConnection connection = new APIConnection(account);
            JsonNode node = connection.getResponse(endpoint.getEndpoint(), prop, endpoint.getRequestMethod() instanceof String ? (String) endpoint.getRequestMethod() : ((String[]) endpoint.getRequestMethod())[0], endpoint.getPermissions());
            if(node == null) return error("Error getting API response.");
            prop = new Properties();
            prop.put("success", true);
            prop.put("output", node.getObject().toString(4));
            return getGson().toJson(prop);
        });
    }

    public static String formatName(String name) {
        boolean cap = true;
        String n = "";
        for(int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if(c == '-') {
                n += " ";
                cap = true;
                continue;
            }
            if(cap) n += Character.toUpperCase(c);
            else n += Character.toLowerCase(c);
            cap = false;
        }
        return n;
    }

    public static String renderAPI(String section, Request request, Response response) {
        return WebModule.render("./source/modules/api/api.pug", new HashMap<String, Object>() {{
            put("section", section);
        }}, request, response);
    }
}
