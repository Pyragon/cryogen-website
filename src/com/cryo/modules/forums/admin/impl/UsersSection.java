package com.cryo.modules.forums.admin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.forums.ForumAdminSection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.Utilities;

import spark.Request;
import spark.Response;

public class UsersSection implements ForumAdminSection {

    @Override
    public String getName() {
        return "users";
    }

    @Override
    public String decode(String action, Request request, Response response) {
        Properties prop = new Properties();
        HashMap<String, Object> model = new HashMap<>();
        switch(action) {
            case "load":
                String html = null;
                try {
                    html = WebModule.render("./source/modules/forums/admin/users/users.jade", model, request, response);
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
            case "load-list":
                int page = Integer.parseInt(request.queryParams("page"));
                if (page <= 0)
                    page = 1;
                int offset = (page - 1) * 10;
                String query = "";
                query = "LIMIT " + offset + ",10";
                ArrayList<Account> users = GlobalConnection.connection().selectList("player_data", null, query, Account.class, new Object[0]);
                model.put("users", users);
                prop.put("success", true);
                prop.put("html", WebModule.render("./source/modules/forums/admin/users/user_list.jade", model,
                        request, response));
                prop.put("pageTotal", (int) Utilities.roundUp(GlobalConnection.connection().selectCount("player_data", null), 10));
                break;
        }
        return Website.getGson().toJson(prop);
    }
    
}