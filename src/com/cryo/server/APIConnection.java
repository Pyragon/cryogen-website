package com.cryo.server;

import com.cryo.Website;
import com.cryo.db.impl.AccountConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.Utilities;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import java.util.Properties;

public class APIConnection {

    private String username;
    private String password;

    private Account account;

    private String token;

    public APIConnection(String username, String password) {
        this.username = username;
        this.password = password;
        generateAPIToken(true);
    }

    public APIConnection(Account account) {
        this.account = account;
    }

    private void generateAPIToken(boolean start) {
        if(Utilities.isNullOrEmpty(username, password)) {
            if(account == null || start) return;
            Object[] data = AccountConnection.connection().handleRequest("add-token", account.getUsername(), System.currentTimeMillis()+ (3 * 60 * 60) * 1000, true);
            if(data == null) return;
            String token = (String) data[0];
            this.token = token;
        };
        Object[] data = GlobalConnection.connection().handleRequest("compare", username, password);
        if(data == null || !((boolean) data[0])) return;
        System.out.println("Generating.");
        JsonNode node = getResponse("/login", new Properties() {{
            put("username", username);
            put("password", password);
        }}, "POST", -1);
        if (node == null) return;
        if (!node.getObject().getBoolean("success")) {
            System.out.println(node.getObject().getString("error"));
            return;
        }
        token = node.getObject().getString("token");
        System.out.println("Token: " + token);
    }

    public JsonNode getResponse(String endpoint, Properties prop, String requestType, int rights) {
        if(prop != null) {
            for (Object key : prop.keySet()) {
                Object value = prop.get(key);
                if(endpoint.contains("/:"+((String) key))){
                    endpoint = endpoint.replace("/:"+((String) key), "/"+value);
                    continue;
                }
            }
        }
        endpoint = "http://" + Website.getProperties().getProperty("api-url") + "" + endpoint;
        HttpRequest body;
        if(requestType.equals("GET")) body = Unirest.get(endpoint);
        else body = Unirest.post(endpoint);
        if(prop != null) {
            for (Object key : prop.keySet()) {
                Object value = prop.get(key);
                if(endpoint.contains("/:"+((String) key))){
                    endpoint = endpoint.replace("/:"+((String) key), "/"+value);
                    continue;
                }
                body = body.queryString((String) key, value);
            }
        }
        if(rights != -1) {
            if(token == null && account == null) return null;
            if(token != null)
                body = body.queryString("token", token);
            else {
                Object[] data = AccountConnection.connection().handleRequest("add-token", account.getUsername(), System.currentTimeMillis()+ (3 * 60 * 60) * 1000, true);
                if(data == null) return null;
                String token = (String) data[0];
                body = body.queryString("token", token);
                this.token = token;
            }
        }
        body = body.queryString("fromWeb", true);
        try {
            HttpResponse<JsonNode> response = body.asJson();
            JsonNode node = response.getBody();
            if(!node.getObject().has("success")) {
                System.out.println(node.toString()+" "+endpoint);
                return null;
            }
            if(!node.getObject().getBoolean("success")) {
                String error = node.getObject().getString("error");
                if(error.equals("Invalid username or password.")) {
                    System.err.println("Invalid username or password passed to APIConnection!");
                    return null;
                }
                if(node.getObject().has("detailed")) {
                    String detailed = node.getObject().getString("detailed");
                    if(detailed != null && detailed.equals("token-expired")) {
                        generateAPIToken(false);
                        return getResponse(endpoint, prop, requestType, rights);
                    } else if(detailed != null && detailed.equals("invalid-account")) {
                        System.err.println("Error loading account.");
                        return null;
                    }
                }
            }
            return node;
        } catch (UnirestException e) {
            System.out.println(endpoint);
            e.printStackTrace();
            try {
                HttpResponse<String> res = body.asString();
                String response = res.getBody();
                System.out.println(response);
            } catch (UnirestException e1) {
                e1.printStackTrace();
            }

        }
        return null;
    }
}
