package com.cryo.modules.forums.admin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.BBCode;
import com.cryo.entities.forums.ForumAdminSection;
import com.cryo.modules.WebModule;
import com.cryo.utils.Utilities;
import com.mysql.jdbc.StringUtils;

import static com.cryo.modules.WebModule.error;

import spark.Request;
import spark.Response;

public class BBCodeSection implements ForumAdminSection {

    public String getName() { return "bbcodes"; }

    public String decode(String action, Request request, Response response) {
        Properties prop = new Properties();
        HashMap<String, Object> model = new HashMap<>();
        switch(action) {
            case "load":
                String html = null;
                try {
                    html = WebModule.render("./source/modules/forums/admin/bbcodes/bbcodes.jade", model, request, response);
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
                ArrayList<BBCode> bbcodes = ForumConnection.connection().selectList("bbcodes", null, query,
                        BBCode.class, new Object[0]);
                model.put("bbcodes", bbcodes);
                prop.put("success", true);
                prop.put("html", WebModule.render("./source/modules/forums/admin/bbcodes/bbcodes_list.jade", model, request,
                        response));
                prop.put("pageTotal",
                        (int) Utilities.roundUp(ForumConnection.connection().selectCount("bbcodes", null), 10));
                break;
            case "view":
                if (!StringUtils.isNullOrEmpty(request.queryParams("id"))) {
                    String idString = request.queryParams("id");
                    int id;
                    try {
                        id = Integer.parseInt(idString);
                    } catch(Exception e) {
                        return error("Error pasing id!");
                    }
                    BBCode code = ForumConnection.connection().selectClass("bbcodes", "id=?", BBCode.class, id);
                    if (code == null)
                        return error("Unable to find a BBCode with that ID.");
                    model.put("code", code);
                }
                model.put("manager", Website.instance().getBBCodeManager());
                html = WebModule.render("./source/modules/forums/admin/bbcodes/edit_bbcode.jade", model, request,
                        response);
                prop.put("success", true);
                prop.put("html", html);
                break;
            case "format":
                String regex = request.queryParams("regex");
                String replacement = request.queryParams("replacement");
                String example = request.queryParams("example");
                prop.put("success", true);
                prop.put("post", Website.instance().getBBCodeManager().getFormattedPost(regex, replacement, example));
                break;
            case "save":
                String name = request.queryParams("name");
                String description = request.queryParams("description");
                String tag = request.queryParams("tag");
                String allowNestedS = request.queryParams("allowNested");
                regex = request.queryParams("regex");
                replacement = request.queryParams("replacement");
                example = request.queryParams("example");
                BBCode code;
                boolean add = true;
                int id = -1;
                boolean allowNested;
                try {
                    allowNested = Boolean.parseBoolean(allowNestedS);
                } catch(Exception e) {
                    return error("Error parsing value: allow_nested");
                }
                if (!StringUtils.isNullOrEmpty(request.queryParams("id"))) {
                    String idString = request.queryParams("id");
                    try {
                        id = Integer.parseInt(idString);
                    } catch(Exception e) {
                        return error("Error pasing id!");
                    }
                    add = false;
                    code = ForumConnection.connection().selectClass("bbcodes", "id=?", BBCode.class, id);
                    if(code == null) return error("Unable to find bbcode for that id");
                    code.setName(name);
                    code.setDescription(description);
                    code.setTag(tag);
                    code.setAllowNested(allowNested);
                    code.setRegex(regex);
                    code.setReplacement(replacement);
                    code.setExample(example);
                } else
                    code = new BBCode(-1, name, description, tag, allowNested, regex, replacement, example, null, null);
                if(add)
                    ForumConnection.connection().insert("bbcodes", code.data());
                else
                    ForumConnection.connection().update("bbcodes", "id=?", code, 
                            new String[] { "name", "description", "tag", "allowNested", "regex", "replacement", "example" }, id);
                Website.instance().getBBCodeManager().load();
                prop.put("success", true);
                break;
        }
        return Website.getGson().toJson(prop);
    }

}