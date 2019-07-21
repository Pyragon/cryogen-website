package com.cryo.modules.forums;

import com.cryo.Website;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.Post;
import com.cryo.entities.forums.SubForum;
import com.cryo.entities.forums.Thread;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.CookieManager;
import com.mysql.jdbc.StringUtils;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

public class ForumsModule extends WebModule {

    public String[] getEndpoints() {
        return new String[] {
                "GET", "/forums/thread/:id",
                "GET", "/forums/post/:id",
                "GET", "/forums/forum/:id",
                "GET", "/forums/forum/:id/new-thread",
                "GET", "/forums/thread/:id/page/:page",
                "GET", "/forums/user/:id",
                "GET", "/forums",
                "POST", "/forums",
                "POST", "/forums/thread/:id/page/:page",
                "POST", "/forums/forum/:id",
                "POST", "/forums/forum/:id/new-thread",
                "POST", "/forums/forum/:id/submit-new-thread",
                "POST", "/forums/thread/:id", //for getting data to make non-refresh page
                "POST", "/forums/post/:id", //for getting data to make non-refresh page (not needed?)
                "POST", "/forums/post", //for posting post
                "POST", "/forums/thread", //for posting thread
                "POST", "/forums/user" //for creating user, use recaptcha, email verification, and cooldowns
        };
    }

    @Override
    public String decodeRequest(String endpoint, Request request, Response response) {
        Properties prop = new Properties();
        HashMap<String, Object> model = new HashMap<>();
        String method = request.requestMethod();
        Account account = CookieManager.getAccount(request);
        switch(endpoint) {
            case "/forums":
                try {
                    model.put("categories", Website.instance().getCachingManager().getData("category-list-cache"));
                    ArrayList<String> breadcrumbs = new ArrayList<String>() {{
                        add("Home");
                    }};
                    ArrayList<String> links = new ArrayList<String>() {{
                        add("/forums");
                    }};
                    model.put("breadcrumbs", breadcrumbs);
                    model.put("links", links);
                    prop.put("breadcrumbs", breadcrumbs);
                    prop.put("links", links);
                    if(request.requestMethod().equals("POST")) {
                        String html = render("./source/modules/forums/main.jade", model, request, response);
                        prop.put("success", true);
                        prop.put("html", html);
                        break;
                    }
                    return render("./source/modules/forums/forums.jade", model, request, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    return error("Error loading");
                }
            case "/forums/forum/:id":
                String idString = request.params(":id");
                int id;
                try {
                    id = Integer.parseInt(idString);
                } catch(Exception e) {
                    return redirect("/forums", "Error loading page, redirecting you back to home.",5, null, request, response);
                }
                Object data = Website.instance().getCachingManager().getData("subforums-cache", id);
                if(data == null) return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request, response);
                SubForum forum = (SubForum) data;
                model.put("forum", forum);
                ArrayList<String> crumbs = new ArrayList<>();
                ArrayList<String> links = new ArrayList<>();
                Object[] cData = forum.createBreadcrumbs(crumbs, links);
                crumbs = (ArrayList<String>) cData[0];
                links = (ArrayList<String>) cData[1];
                Collections.reverse(crumbs);
                Collections.reverse(links);
                model.put("breadcrumbs", crumbs);
                model.put("links", links);
                prop.put("breadcrumbs", crumbs);
                prop.put("links", links);
                if(request.requestMethod().equals("GET"))
                    return render("./source/modules/forums/forums.jade", model, request, response);
                prop.put("success", true);
                prop.put("html", render("./source/modules/forums/forum.jade", model, request, response));
                break;
            case "/forums/forum/:id/new-thread":
                if(account == null)
                    return showLoginPage(endpoint, request, response);
                idString = request.params(":id");
                try {
                    id = Integer.parseInt(idString);
                } catch(Exception e) {
                    return redirect("/forums", "Error loading page, redirecting you back to home.",5, null, request, response);
                }
                data = Website.instance().getCachingManager().getData("subforums-cache", id);
                if(data == null) return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request, response);
                forum = (SubForum) data;
                crumbs = new ArrayList<>();
                links = new ArrayList<>();
                crumbs.add("New Thread");
                links.add("");
                cData = forum.createBreadcrumbs(crumbs, links);
                crumbs = (ArrayList<String>) cData[0];
                links = (ArrayList<String>) cData[1];
                Collections.reverse(crumbs);
                Collections.reverse(links);
                model.put("breadcrumbs", crumbs);
                model.put("links", links);
                prop.put("breadcrumbs", crumbs);
                prop.put("links", links);
                if(request.requestMethod().equals("GET"))
                    return render("./source/modules/forums/forums.jade", model, request, response);
                prop.put("success", true);
                prop.put("html", render("./source/modules/forums/new-thread.jade", model, request, response));
                break;
            case "/forums/thread/:id":
                return loadThread(1, request, response);
            case "/forums/thread/:id/page/:page":
                String pageString = request.params(":page");
                int page;
                try {
                    page = Integer.parseInt(pageString);
                } catch(Exception e) {
                    if(request.requestMethod().equals("GET"))
                        return redirect("/forums", "Invalid page found. Redirecting to home", 5, null, request, response);
                    return error("Invalid page.");
                }
                idString = request.params(":id");
                try {
                    id = Integer.parseInt(idString);
                } catch (Exception e) {
                    return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
                }
                Thread thread = ForumConnection.connection().selectClass("threads", "id=?", Thread.class, id);
                if(thread == null) return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
                if(request.requestMethod().equals("GET"))
                    return loadThread(thread, page, request, response);
                else {
                    prop.put("success", true);
                    model.put("posts", thread.getPosts(page));
                    prop.put("html", render("./source/modules/forums/post-list.jade", model, request, response));
                }
                break;
            case "/forums/forum/:id/submit-new-thread":
                int insertId;
                try {
                    if (request.requestMethod().equals("GET"))
                        return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request, response);
                    if(account == null) {
                        if (request.requestMethod().equals("GET"))
                            return redirect("/forums", "Not logged in. Redirecting you back to home.", 5, null, request, response);
                        else
                            return error("Not logged in. Please refresh the page and try again.");
                    }
                    idString = request.params(":id");
                    try {
                        id = Integer.parseInt(idString);
                    } catch (Exception e) {
                        return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
                    }
                    data = Website.instance().getCachingManager().getData("subforums-cache", id);
                    if (data == null)
                        return redirect("/community", "Invalid request. Redirecting you back to home.", 5, null, request, response);
                    forum = (SubForum) data;
                    String title = request.queryParams("title");
                    String body = request.queryParams("body");
                    if (StringUtils.isNullOrEmpty(title) || title.length() < 5 || title.length() > 50)
                        return error("Title must be between 5 and 50 characters long.");
                    if (StringUtils.isNullOrEmpty(body) || body.length() < 5 || body.length() > 50)
                        return error("Body must be between 5 and 50 characters long.");
                    //TODO - get user id, set MySQLDefault in thread class
                    //TODO - allow MySQLDefault to provide a default value
                    thread = new Thread(-1, forum.getId(), title, account.getId(), -1, -1, null, false, -1, null, null);
                    insertId = ForumConnection.connection().insert("threads", thread.data());
                    Post post = new Post(-1, insertId, account.getId(), body, null, null);
                    insertId = ForumConnection.connection().insert("posts", post.data());
                    post = ForumConnection.connection().selectClass("posts", "id=?", Post.class, insertId);
                    thread.updateLastPost(post);
                } catch(Exception e) {
                    e.printStackTrace();
                    return error("An error has occurred.");
                }
                return redirect("/forums/thread/"+thread.getId(), "You are now being redirected to your new thread.", 5, null, request, response);
            default:
                return error("Error decoding request: "+endpoint);
        }
        return Website.getGson().toJson(prop);
    }

    public String loadThread(int page, Request request, Response response) {
        String idString = request.params(":id");
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (Exception e) {
            return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
        }
        Thread thread = ForumConnection.connection().selectClass("threads", "id=?", Thread.class, id);
        return loadThread(thread, page, request, response);
    }

    public String loadThread(Thread thread, int page, Request request, Response response) {
        if(thread == null) return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        Properties prop = new Properties();
        SubForum forum = thread.getSubForum();
        if(forum == null) return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
        model.put("thread", thread);
        model.put("page", page);
        ArrayList crumbs = new ArrayList<>();
        ArrayList links = new ArrayList<>();
        crumbs.add(thread.getTitle());
        links.add("");
        Object[] cData = forum.createBreadcrumbs(crumbs, links);
        crumbs = (ArrayList<String>) cData[0];
        links = (ArrayList<String>) cData[1];
        Collections.reverse(crumbs);
        Collections.reverse(links);
        model.put("breadcrumbs", crumbs);
        model.put("links", links);
        prop.put("breadcrumbs", crumbs);
        prop.put("links", links);
        if(request.requestMethod().equals("GET"))
            return render("./source/modules/forums/forums.jade", model, request, response);
        prop.put("success", true);
        prop.put("html", render("./source/modules/forums/thread.jade", model, request, response));
        return Website.getGson().toJson(prop);
    }

    @Override
    public Object decodeRequest(Request request, Response response, Website.RequestType type) {

        return null;
    }
}
