package com.cryo.modules.forums.admin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cryo.Website;
import com.cryo.db.impl.DisplayConnection;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.forums.ForumAdminSection;
import com.cryo.entities.forums.UserGroup;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.BCrypt;
import com.cryo.utils.Utilities;
import com.mysql.jdbc.StringUtils;

import static com.cryo.modules.WebModule.error;

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
            case "edit":
                if (!StringUtils.isNullOrEmpty(request.queryParams("id"))) {
                    String idString = request.queryParams("id");
                    int id;
                    try {
                        id = Integer.parseInt(idString);
                    } catch (Exception e) {
                        return error("Error pasing id!");
                    }
                    Account account = GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, id);
                    if (account == null)
                        return error("Unable to find a user with that ID.");
                    model.put("editing", account);
                }
                model.put("groups", ForumConnection.connection().selectList("usergroups", UserGroup.class));
                html = WebModule.render("./source/modules/forums/admin/users/edit_user.jade", model, request,
                        response);
                prop.put("success", true);
                prop.put("html", html);
                break;
            case "remove":
                String idString = request.queryParams("id");
                int id;
                try {
                    id = Integer.parseInt(idString);
                } catch (Exception e) {
                    return error("Error pasing id!");
                }
                Account account = GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, id);
                if (account == null)
                    return error("Cannot find user with that ID.");
                if(account.getUsername().equals("cody")) return error("You cannot delete Cody's account.");
                GlobalConnection.connection().delete("player_data", "id=?", account.getId());
                prop.put("success", true);
                break;
            case "save":
                String username = request.queryParams("username");
                String displayName = request.queryParams("displayName");
                String password = request.queryParams("password");
                String rightsS = request.queryParams("rights");
                String donatorS = request.queryParams("donator");
                String avatar = request.queryParams("avatar");
                String email = request.queryParams("email");
                String displaygroupS = request.queryParams("displaygroup");
                String usergroupsS = request.queryParams("usergroups");
                int rights, donator, displaygroupId;
                boolean add = StringUtils.isNullOrEmpty(request.queryParams("id"));
                try {
                    rights = Integer.parseInt(rightsS);
                    donator = Integer.parseInt(donatorS);
                    displaygroupId = Integer.parseInt(displaygroupS);
                } catch(Exception e) {
                    return error("Error parsing values! Please try again.");
                }
                UserGroup displaygroup = ForumConnection.connection().selectClass("usergroups", "id=?", UserGroup.class, displaygroupId);
                if(displaygroup == null) return error("Invalid displaygroup! Please try again.");
                if(!usergroupsS.equals("")) {
                    List<UserGroup> usergroups;
                    String[] split = usergroupsS.split(", ?");
                    try {
                        usergroups = Stream.of(split)
                                        .map(s -> ForumConnection.connection().selectClass("usergroups", "id=?", UserGroup.class, Integer.parseInt(s)))
                                        .collect(Collectors.toList());
                    } catch(Exception e) {
                        return error("Error pasing usergroups. Please try again.");
                    }
                }
                if (add && (password == null || password.equals("")))
                    return error("You must enter a password when creating a new account.");
                if(!StringUtils.isNullOrEmpty(request.queryParams("id"))) {
                    idString = request.queryParams("id");
                    try {
                        id = Integer.parseInt(idString);
                    } catch (Exception e) {
                        return error("Error pasing id!");
                    }
                    account = GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, id);
                    if(account == null) return error("Cannot find user with that ID.");
                    if(account.getUsername().equals("cody")) return error("You cannot edit Cody's account.");
                    if(!account.getUsername().equals(username)) prop.put("message", "You cannot edit usernames. Please create a new user.");
                    account.setRights(rights);
                    account.setDonator(donator);
                    account.setAvatarUrl(avatar);
                    account.setDisplayGroup(displaygroup.getId());
                    account.setUsergroups(usergroupsS.replace(" ", ""));
                    String current = account.getDisplayName();
                    if(!displayName.equals(current)) {
                        DisplayConnection.connection().set("last_names", "display_name=?", "username=?", current, account.getUsername());
                        DisplayConnection.connection().set("current_names", "display_name=?", "username=?", displayName, username);
                    }
                    if(email != null && !email.equals(account.getEmail()))
                        account.setEmail(email);
                    String salt = account.getSalt();
                    String hash = BCrypt.hashPassword(password, salt);
                    if(!hash.equals(account.getPassword()))
                        account.setPassword(hash);
                } else {
                    String salt = BCrypt.generate_salt();
                    String hash = BCrypt.hashPassword(password, salt);
                    account = new Account(-1, username, hash, salt, rights, donator, avatar, displaygroup.getId(), usergroupsS.replace(" ", ""), null);
                    DisplayConnection.connection().insert("last_names", username, displayName);
                    DisplayConnection.connection().insert("current_names", username, displayName);
                    if(email != null && !email.equals(""))
                        account.setEmail(email);
                }
                if(add)
                    GlobalConnection.connection().insert("player_data", account.data());
                else
                    GlobalConnection.connection().update("player_data", "id=?", account, 
                        new String[] { "password", "salt", "rights", "donator", "avatarUrl", "displayGroup", "usergroups" }, account.getId());
                prop.put("success", true);
                break;
        }
        return Website.getGson().toJson(prop);
    }
    
}