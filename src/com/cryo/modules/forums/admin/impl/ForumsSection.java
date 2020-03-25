package com.cryo.modules.forums.admin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Website;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.ForumAdminSection;
import com.cryo.entities.forums.SubForum;
import com.cryo.modules.WebModule;
import com.mysql.jdbc.StringUtils;

import static com.cryo.modules.WebModule.error;

import spark.Request;
import spark.Response;

public class ForumsSection implements ForumAdminSection {

    public String getName() { return "forums"; }

    public String decode(String action, Request request, Response response) {
        Properties prop = new Properties();
        HashMap<String, Object> model = new HashMap<>();
        switch(action) {
            case "load":
                String html = null;
                model.put("categories", ForumConnection.connection().selectList("subforums", "parent_id=?", "ORDER BY priority ASC", SubForum.class, -1));
                try {
                    html = WebModule.render("./source/modules/forums/admin/forums/forums.jade", model, request, response);
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
            case "view":
                if (!StringUtils.isNullOrEmpty(request.queryParams("id"))) {
                    String idString = request.queryParams("id");
                    int id;
                    try {
                        id = Integer.parseInt(idString);
                    } catch (Exception e) {
                        return error("Error pasing id!");
                    }
                    SubForum forum = ForumConnection.connection().selectClass("subforums", "id=?", SubForum.class, id);
                    if (forum == null)
                        return error("Unable to find a forum with that ID.");
                    model.put("forum", forum);
                }
                System.out.println(request.queryParams("id"));
                if(!StringUtils.isNullOrEmpty(request.queryParams("parent")))
                    model.put("parent", request.queryParams("parent"));
                html = WebModule.render("./source/modules/forums/admin/forums/edit_forum.jade", model, request,
                        response);
                prop.put("success", true);
                prop.put("html", html);
                break;
             case "save":
                String name = request.queryParams("name");
                String description = request.queryParams("description");
                String parentS = request.queryParams("parent");
                String category = request.queryParams("category");
                String link = request.queryParams("link");
                String priorityS = request.queryParams("priority");
                boolean isCategory = false;
                int id = -1, parent, priority;
                try {
                    isCategory = Boolean.parseBoolean(category);
                    parent = Integer.parseInt(parentS);
                    priority = Integer.parseInt(priorityS);
                } catch(Exception e) {
                    return error("Error parsing values");
                }
                SubForum forum;
                boolean add = true;
                if(!StringUtils.isNullOrEmpty(request.queryParams("id"))) {
                    String idString = request.queryParams("id");
                    try {
                        id = Integer.parseInt(idString);
                    } catch(Exception e) {
                        return error("Error parsing ID");
                    }
                    add = false;
                    forum = ForumConnection.connection().selectClass("subforums", "id=?", SubForum.class, id);
                    if(forum == null) return error("Error finding forum with that ID.");
                    forum.setName(name);
                    forum.setDescription(description);
                    forum.setParentId(parent);
                    forum.setCategory(isCategory);
                    if(link != null && !link.equals(""))
                        forum.setLink(link);
                } else forum = new SubForum(-1, name, description, parent, isCategory, 2, getNextPriority(parent), link, null, null);
                if(add) ForumConnection.connection().insert("subforums", forum.data());
                else ForumConnection.connection().update("subforums", "id=?", forum, new String[] {
                    "name", "description", "parentId", "isCategory", "link"
                 }, id);
                editPriority(forum, priority);
                prop.put("success", true);
                model.put("categories", ForumConnection.connection().selectList("subforums", "parent_id=?", "ORDER BY priority ASC", SubForum.class, -1));
                prop.put("html", WebModule.render("./source/modules/forums/admin/forums/forums_list.jade", model, request, response));
                break;
        }
        return Website.getGson().toJson(prop);
    }

    public static void editPriority(SubForum toInsert, int newPriority) {
        try {
            ArrayList<SubForum> forums = new ArrayList<>();
            if(toInsert.getParent() != null)
                forums.addAll(toInsert.getParent().getSubForums());
            else forums = ForumConnection.connection().selectList("subforums", "parent_id=?", SubForum.class, -1);
            forums.remove(toInsert.getPriority());
            if(newPriority >= forums.size()) forums.add(toInsert);
            else forums.set(newPriority, toInsert);
            int index = 0;
            for(SubForum forum : forums) {
                int priority = index++;
                ForumConnection.connection().set("subforums", "priority=?", "id=?", priority, forum.getId());
                System.out.println("Setting "+forum.getName()+" to: "+ priority);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static int getNextPriority(int parent) {
        ArrayList<SubForum> subs = ForumConnection.connection().selectList("subforums", "parent_id=?", SubForum.class, parent);
        int lastPriority = -1;
        for(SubForum forum : subs)
            if(forum.getPriority() > lastPriority) lastPriority = forum.getPriority();
        return lastPriority+1;
    }

}