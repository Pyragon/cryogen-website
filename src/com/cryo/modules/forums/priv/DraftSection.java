package com.cryo.modules.forums.priv;

import com.cryo.Website;
import com.cryo.db.impl.AccountConnection;
import com.cryo.entities.forums.Draft;
import com.cryo.entities.forums.PrivateSection;
import com.cryo.entities.forums.SentMessage;
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

public class DraftSection implements PrivateSection {
    @Override
    public String getName() {
        return "drafts";
    }

    @Override
    public String decode(String action, Request request, Response response) {
        Properties prop = new Properties();
        if(!CookieManager.isLoggedIn(request))
            return WebModule.showLoginPage("/forums/private/drafts", request, response);
        Account account = CookieManager.getAccount(request);
        HashMap<String, Object> model = new HashMap<>();
        switch(action) {
            case "load":
                try {
                    String html = WebModule.render("./source/modules/forums/private/drafts/drafts.jade", model, request, response);
                    prop.put("success", true);
                    prop.put("html", html);
                } catch (Exception e) {
                    return error("Error loading drafts.");
                }
                break;
            case "load-list":
                int page = Integer.parseInt(request.queryParams("page"));
                if(page == 0) page = 1;
                int offset = (page - 1) * 10;
                List<Draft> drafts = AccountConnection.connection().selectList("drafts", "account_id=?", "LIMIT "+offset+",10", Draft.class, account.getId());
                if(drafts == null) return error("Error loading private messages.");
                int count = AccountConnection.connection().selectCount("drafts", "account_id=?", account.getId());
                count = (int) Utilities.roundUp(count, 10);
                model.put("drafts", drafts);
                model.put("type", "drafts");
                String html = WebModule.render("./source/modules/forums/private/drafts/draft_list.jade", model, request, response);
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
                Draft message = AccountConnection.connection().selectClass("drafts", "id=?", Draft.class, id);
                if(message == null || message.getAccountId() != account.getId())
                    return error("Unable to find that message. Please try again.");
                AccountConnection.connection().delete("drafts", "id=?", id);
                prop.put("success", true);
                break;
        }
        return Website.getGson().toJson(prop);
    }
}
