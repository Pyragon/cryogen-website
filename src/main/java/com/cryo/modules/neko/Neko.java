package com.cryo.modules.neko;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoint;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.google.gson.internal.LinkedTreeMap;
import com.mysql.cj.util.StringUtils;
import spark.Request;
import spark.Response;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

import static com.cryo.utils.Utilities.error;
import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Neko {

    @SPAEndpoint("/neko")
    public static String renderMovieNightPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        try {
            if (account == null)
                return Login.renderLoginPage("/neko", request, response);
            String sessionId = request.cookie("cryo_sess");
            if (sessionId == null)
                sessionId = request.session().attribute("cryo_sess");
            HashMap<String, Object> model = new HashMap<>();
            model.put("sessionId", sessionId);
            return renderPage("neko/index", model, request, response);
        } catch(Exception e) {
            e.printStackTrace();
            return error("Test");
        }
    }

    @Endpoint(method = "POST", endpoint = "/neko/member-list")
    public static String renderMemberList(Request request, Response response) {
        if(AccountUtils.getAccount(request) == null)
            return Login.renderLoginPage("/neko", request, response);
        if(!request.queryParams().contains("members"))
            return error("Unable to parse members. Please refresh the page and try again.");
        String membersStr = request.queryParams("members");
        ArrayList<LinkedTreeMap<String, Object>> members;
        try {
            members = Website.getGson().fromJson(membersStr, ArrayList.class);
        } catch(Exception e) {
            e.printStackTrace();
            return error("Unable to parse members. Please refresh the page and try again.");
        }
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Account> accounts = new ArrayList<>();
        for(LinkedTreeMap<String, Object> map : members) {
            Account account = AccountUtils.getAccount((String) map.get("displayname"));
            if(account == null) continue;
            accounts.add(account);
        }
        model.put("members", accounts);
        return renderPage("neko/member-list", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/neko/chat")
    public static String renderChat(Request request, Response response) {
        if(AccountUtils.getAccount(request) == null)
            return Login.renderLoginPage("/neko", request, response);
        if(!request.queryParams().contains("messages"))
            return error("Unable to parse messages. Please refresh the page and try again.");
        String messagesStr = request.queryParams("messages");
        ArrayList<LinkedTreeMap<String, Object>> messages;
        try {
            messages = Website.getGson().fromJson(messagesStr, ArrayList.class);
        } catch(Exception e) {
            e.printStackTrace();
            return error("Unable to parse messages. Please refresh the page and try again.");
        }
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Properties> results = new ArrayList<>();
        messages.forEach(m -> {
            Account account = AccountUtils.getAccount((String) m.get("username"));
            if(account == null) return;
            Properties prop = new Properties();
            prop.put("content", m.get("content"));
            prop.put("stamp", new Timestamp((long) (double) m.get("stamp")));
            prop.put("account", account);
            results.add(prop);
        });
        model.put("messages", results);
        return renderPage("neko/chat", model, request, response);
    }

}
