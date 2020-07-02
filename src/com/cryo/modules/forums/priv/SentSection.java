package com.cryo.modules.forums.priv;

import com.cryo.Website;
import com.cryo.db.impl.AccountConnection;
import com.cryo.entities.forums.InboxMessage;
import com.cryo.entities.forums.SentMessage;
import com.cryo.entities.forums.PrivateSection;
import com.cryo.managers.CookieManager;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.Utilities;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static com.cryo.Website.error;

public class SentSection implements PrivateSection {
    @Override
    public String getName() {
        return "sent";
    }

    @Override
    public String decode(String action, Request request, Response response) {
        Properties prop = new Properties();
        if(!CookieManager.isLoggedIn(request))
            return WebModule.showLoginPage("/forums/private/sent", request, response);
        Account account = CookieManager.getAccount(request);
        HashMap<String, Object> model = new HashMap<>();
        switch(action) {
            case "load":
                try {
                    String html = WebModule.render("./source/modules/forums/private/sent/sent.jade", model, request, response);
                    prop.put("success", true);
                    prop.put("html", html);
                } catch (Exception e) {
                    return error("Error loading sent messages.");
                }
                break;
            case "load-list":
                int page = Integer.parseInt(request.queryParams("page"));
                if(page == 0) page = 1;
                int offset = (page - 1) * 10;
                List<SentMessage> messages = AccountConnection.connection().selectList("sent", "account_id=?", "LIMIT "+offset+",10", SentMessage.class, account.getId());
                if(messages == null) return error("Error loading private messages.");
                int count = AccountConnection.connection().selectCount("sent", "account_id=?", account.getId());
                count = (int) Utilities.roundUp(count, 10);
                model.put("messages", messages);
                model.put("type", "sent");
                String html = WebModule.render("./source/modules/forums/private/sent/sent_list.jade", model, request, response);
                prop.put("success", true);
                prop.put("html", html);
                prop.put("pageTotal", count);
                break;
            case "delete-message":
                String idString = request.queryParams("id");
                int id;
                try {
                    id = Integer.parseInt(idString);
                } catch(Exception e) {
                    return error("Error parsing id. please try again later.");
                }
                SentMessage message = AccountConnection.connection().selectClass("sent", "id=?", SentMessage.class, id);
                if(message == null || message.getAccountId() != account.getId())
                    return error("Unable to find that message. Please try again.");
                AccountConnection.connection().delete("sent", "id=?", id);
                prop.put("success", true);
                break;
        }
        return Website.getGson().toJson(prop);
    }
}
