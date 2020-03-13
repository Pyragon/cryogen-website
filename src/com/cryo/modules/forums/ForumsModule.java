package com.cryo.modules.forums;

import com.cryo.Website;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.forums.Post;
import com.cryo.entities.forums.SubForum;
import com.cryo.entities.forums.Thanks;
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
                "POST", "/forums/stats",
                "POST", "/forums/thread/:id/page/:page",
                "POST", "/forums/forum/:id",
                "POST", "/forums/forum/:id/new-thread",
                "POST", "/forums/forum/:id/submit-new-thread",
                "POST", "/forums/thread/:id", //for getting data to make non-refresh page
                "POST", "/forums/post/:id", //for getting data to make non-refresh page (not needed?)
                "POST", "/forums/post/:id/addremove-thanks",
                "POST", "/forums/thread/:id/submit-new-post", //for posting post
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
                    if(account != null)
                        account.setStatus(1, 0, 0, 0);
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
            case "/forums/stats":
                prop.put("success", true);
                prop.put("html", render("./source/modules/forums/forum_stats.jade", model, request, response));
                break;
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
                if(account != null)
                    account.setStatus(0, forum.getId(), 0, 0);
                if(request.requestMethod().equals("GET"))
                    return render("./source/modules/forums/forums.jade", model, request, response);
                prop.put("success", true);
                prop.put("html", render("./source/modules/forums/forum.jade", model, request, response));
                break;
            case "/forums/forum/:id/new-thread":
                if (account == null) {
                    if (request.requestMethod().equals("GET"))
                        return showLoginPage(endpoint, request, response);
                    else return error("You must be logged in to create a new post.");
                }
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
                prop.put("html", render("./source/modules/forums/new_thread.jade", model, request, response));
                break;
            case "/forums/thread/:id":
                return loadThread(account, 1, request, response);
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
                    return loadThread(account, thread, page, request, response);
                else {
                    prop.put("success", true);
                    model.put("posts", thread.getPosts(page));
                    prop.put("html", render("./source/modules/forums/post_list.jade", model, request, response));
                }
                break;
            case "/forums/forum/:id/submit-new-thread":
                int threadId;
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
                    if (StringUtils.isNullOrEmpty(body) || body.length() < 5 || body.length() > 10_000)
                        return error("Body must be between 5 and 10,000 characters long.");
                    thread = new Thread(-1, forum.getId(), title, account.getId(), -1, -1, -1, null, false, -1, 0, null, null);
                    threadId = ForumConnection.connection().insert("threads", thread.data());
                    thread.setId(threadId);
                    Post post = new Post(-1, threadId, account.getId(), body, null, null, null);
                    int insertId = ForumConnection.connection().insert("posts", post.data());
                    post = ForumConnection.connection().selectClass("posts", "id=?", Post.class, insertId);
                    thread.updateLastPost(post);
                    thread.updateFirstPost(post);
                } catch(Exception e) {
                    e.printStackTrace();
                    return error("An error has occurred.");
                }
                return redirect("/forums/thread/"+thread.getId(), "You are now being redirected to your new thread.", 5, null, request, response);
            case "/forums/thread/:id/submit-new-post":
                if (request.requestMethod().equals("GET"))
                    return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request,
                            response);
                if (account == null) {
                    if (request.requestMethod().equals("GET"))
                        return redirect("/forums", "Not logged in. Redirecting you back to home.", 5, null, request,
                                response);
                    else
                        return error("Not logged in. Please refresh the page and try again.");
                }
                idString = request.params(":id");
                pageString = request.queryParams("page");
                try {
                    id = Integer.parseInt(idString);
                    page = Integer.parseInt(pageString);
                } catch (Exception e) {
                    return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request,
                            response);
                }
                thread = ForumConnection.connection().selectClass("threads", "id=?", Thread.class, id);
                if(thread == null) return error("Error finding thread. Please refresh and try again.");
                String body = request.queryParams("body");
                if (StringUtils.isNullOrEmpty(body) || body.length() < 5 || body.length() > 10_000)
                    return error("Body must be between 5 and 10,000 characters long.");
                System.out.println(body);
                Post post = new Post(-1, id, account.getId(), body, null, null, null);
                int insertId = ForumConnection.connection().insert("posts", post.data());
                post = ForumConnection.connection().selectClass("posts", "id=?", Post.class, insertId);
                thread.updateLastPost(post);
                prop.put("success", true);
                model.put("posts", thread.getPosts(page));
                prop.put("html", render("./source/modules/forums/post_list.jade", model, request, response));
                break;
            case "/forums/post/:id/addremove-thanks":
                if(account == null) return error("You must be logged in to do this.");
                idString = request.params(":id");
                boolean removing;
                try {
                    id = Integer.parseInt(idString);
                    removing = Boolean.parseBoolean(request.queryParams("removing"));
                } catch (Exception e) {
                    return error("Error thanking. Please refresh page and try again.");
                }
                post = ForumConnection.connection().selectClass("posts", "id=?", Post.class, id);
                if(post == null)
                    return error("Error finding post. Please refresh page and try again.");
                if(post.getAuthorId() == account.getId()) return error("You cannot thank your own post.");
                if(removing)
                    ForumConnection.connection().delete("thanks", "post_id=? && account_id=?", post.getId(), account.getId());
                else {
                    Thanks thanks = ForumConnection.connection().selectClass("thanks", "post_id=? && account_id=?", Thanks.class, post.getId(), account.getId());
                    if (thanks != null)
                        return error("You have already thanked this post.");
                    ForumConnection.connection().insert("thanks", "DEFAULT", post.getId(), post.getAuthorId(), account.getId(), "DEFAULT");
                }
                Website.instance().getCachingManager().clear("thanks-cache");
                model.put("post", post);
                prop.put("success", true);
                prop.put("html", render("./source/modules/forums/thanks_list.jade", model, request, response));
                break;
            default:
                return Website.render404(request, response);
        }
        return Website.getGson().toJson(prop);
    }

    public String loadThread(Account account, int page, Request request, Response response) {
        String idString = request.params(":id");
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (Exception e) {
            return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
        }
        Thread thread = ForumConnection.connection().selectClass("threads", "id=?", Thread.class, id);
        return loadThread(account, thread, page, request, response);
    }

    public String loadThread(Account account, Thread thread, int page, Request request, Response response) {
        if(thread == null) return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        Properties prop = new Properties();
        SubForum forum = thread.getSubForum();
        if(forum == null) return redirect("/forums", "Error loading page, redirecting you back to home.", 5, null, request, response);
        model.put("thread", thread);
        model.put("page", page);
        model.put("posts", thread.getPosts(page));
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
        if(account != null)
            account.setStatus(0, 0, 0, thread.getId());
        if(request.requestMethod().equals("GET"))
            return render("./source/modules/forums/forums.jade", model, request, response);
        thread.addView();
        prop.put("success", true);
        prop.put("html", render("./source/modules/forums/thread.jade", model, request, response));
        return Website.getGson().toJson(prop);
    }

    @Override
    public Object decodeRequest(Request request, Response response, Website.RequestType type) {

        return null;
    }
}
