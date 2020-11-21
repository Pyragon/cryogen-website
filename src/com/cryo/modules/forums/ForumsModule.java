package com.cryo.modules.forums;

import com.cryo.Website;
import com.cryo.db.impl.AccountConnection;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.Notified;
import com.cryo.entities.forums.*;
import com.cryo.entities.forums.Thread;
import com.cryo.managers.NotificationManager;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.managers.CookieManager;
import com.cryo.utils.DisplayNames;
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
                "POST", "/forums/post/:id",
                "POST", "/forums/post/:id/addremove-thanks",
                "POST", "/forums/post/:id/edit",
                "POST", "/forums/post/:id/submit-edit",
                "POST", "/forums/thread/:id/submit-new-post", //for posting post
                "POST", "/forums/user", //for creating user, use recaptcha, email verification, and cooldowns
                "POST", "/forums/user/:id",
                "POST", "/forums/user/:id/post-vmessage",
                "POST", "/forums/thread/:id/pin",
                "POST", "/forums/thread/:id/close",
                "POST", "/forums/thread/:id/remove"
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
                    model.put("categories", ForumConnection.connection().selectList("subforums", "parent_id=?", "ORDER BY priority ASC", SubForum.class, -1));
                    ArrayList<String> breadcrumbs = new ArrayList<String>() {{
                        add("Home");
                    }};
                    ArrayList<String> links = new ArrayList<String>() {{
                        add("/forums");
                    }};
                    model.put("breadcrumbs", breadcrumbs);
                    model.put("links", links);
                    if(account != null)
                        account.setStatus(1, -1, -1, -1);
                    if(request.requestMethod().equals("POST")) {
                        String html = render("./source/modules/forums/main.jade", model, request, response);
                        prop.put("success", true);
                        prop.put("html", html);
                        prop.put("breadcrumbs", breadcrumbs.toArray());
                        prop.put("links", links.toArray());
                        break;
                    }
                    return render("./source/modules/forums/forums.jade", model, request, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    return error("Error loading");
                }
            case "/forums/stats":
                prop.put("success", true);
                prop.put("html", render("./source/modules/forums/widgets/forum_stats.jade", model, request, response));
                break;
            case "/forums/forum/:id":
                String idString = request.params(":id");
                int id;
                try {
                    id = Integer.parseInt(idString);
                } catch(Exception e) {
                    return redirect("/forums", "Error loading page, redirecting you back to home.",5, null, request, response);
                }
                return loadForum(account, id, request, response);
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
                    if(request.requestMethod().equals("GET"))
                        return redirect("/forums", "Error loading page, redirecting you back to home.",5, null, request, response);
                    else return error("Invalid Forum ID.");
                }
                Object data = Website.instance().getCachingManager().getData("subforums-cache", id);
                if(data == null) return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request, response);
                SubForum forum = (SubForum) data;
                if (!forum.getPermissions().canCreateThread(account)) {
                    if(request.requestMethod().equals("GET"))
                        return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request, response);
                    else return error("Invalid permissions to create new thread.");
                }
                ArrayList<String> crumbs = new ArrayList<>();
                ArrayList<String> links = new ArrayList<>();
                crumbs.add("New Thread");
                links.add("");
                Object[] cData = forum.createBreadcrumbs(crumbs, links);
                crumbs = (ArrayList<String>) cData[0];
                links = (ArrayList<String>) cData[1];
                Collections.reverse(crumbs);
                Collections.reverse(links);
                model.put("breadcrumbs", crumbs);
                model.put("links", links);
                String defaultMessage = "";
                if(request.queryParams().contains("defaultMessage"))
                    defaultMessage = request.queryParams("defaultMessage");
                model.put("defaultMessage", defaultMessage);
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
            case "/forums/thread/:id/pin":
            case "/forums/thread/:id/close":
            case "/forums/thread/:id/remove":
                if(account == null || account.getRights() == 0)
                    return error("Insufficient permissions.");
                idString = request.params(":id");
                try {
                    id = Integer.parseInt(idString);
                } catch(Exception e) {
                    return error("Unable to parse ID.");
                }
                String action = endpoint.replace("/forums/thread/:id/", "");
                if(!request.queryParams().contains("value") && !action.equals("remove"))
                    return error("Invalid action.");
                thread = ForumConnection.connection().selectClass("threads", "id=?", Thread.class, id);
                if(thread == null) return error("Unable to find thread. Please reload the page and try again.");
                if(!action.equals("remove")) {
                    String valueString = request.queryParams("value");
                    boolean value;
                    try {
                        value = Boolean.parseBoolean(valueString);
                    } catch(Exception e) {
                        return error("Error parsing value.");
                    }
                    pageString = request.queryParams("page");
                    try {
                        page = Integer.parseInt(pageString);
                    } catch(Exception e) {
                        if(request.requestMethod().equals("GET"))
                            return redirect("/forums", "Invalid page found. Redirecting to home", 5, null, request, response);
                        return error("Invalid page.");
                    }
                    if(action.equals("pin")) thread.updatePinned(value);
                    else thread.setStatus(value);
                    return loadThread(account, thread, page, request, response);
                }
                if(account.getRights() == 1 && thread.getAuthor().getRights() > 0 && account.getId() != thread.getAuthor().getId())
                    return error("You cannot remove threads made by other staff.");
                thread.archive();
                return loadForum(account, thread.getForumId(), request, response);
            case "/forums/user/:id":
                //allow name or id
                idString = request.params(":id");
                boolean usingId;
                boolean usingPost = request.requestMethod().equals("POST");
                Account viewing = null;
                try {
                    id = Integer.parseInt(idString);
                    usingId = true;
                } catch(Exception e) {
                    String username = DisplayNames.getUsername(idString);
                    if(username == null) {
                        if(usingPost)
                            return error("Unable to find user with that ID.");
                        else
                            return redirect("/forums", "Unable to find user, redirecting you back to home.", 5, null, request, response);
                    }
                    viewing = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, username);
                    if(viewing == null) {
                        if(usingPost)
                            return error("Unable to find user with that ID.");
                        else
                            return redirect("/forums", "Unable to find user, redirecting you back to home.", 5, null, request, response);
                    }
                    id = viewing.getId();
                    usingId = false;
                }
                if(usingId)
                    viewing = GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, id);
                if(viewing == null) {
                    if(usingPost)
                        return error("Unable to find user with that ID.");
                    else
                        return redirect("/forums", "Unable to find user, redirecting you back to home.", 5, null, request, response);
                }
                if(account != null)
                    account.setStatus(-1, -1, viewing.getId(), -1);
                model.put("account", viewing);
                model.put("vmessages", viewing.getVisitorMessages());
                try {
                    if (usingPost) {
                        prop.put("success", true);
                        prop.put("html", render("./source/modules/forums/user/user_profile.jade", model, request, response));
                    } else return render("./source/modules/forums/forums.jade", model, request, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    return error("Error.");
                }
                break;
            case "/forums/user/:id/post-vmessage":
                //allow name or id
                idString = request.params(":id");
                viewing = null;
                try {
                    id = Integer.parseInt(idString);
                    usingId = true;
                } catch(Exception e) {
                    String username = DisplayNames.getUsername(idString);
                    if(username == null)
                        return error("Unable to find user with that ID.");
                    viewing = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, username);
                    if(viewing == null)
                        return error("Unable to find user with that ID.");
                    id = viewing.getId();
                    usingId = false;
                }
                if(usingId)
                    viewing = GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, id);
                if(viewing == null)
                    return error("Unable to find user with that ID.");
                //TODO - check permissions and shit. Can we post on their wall?
                if(account == null) return error("You must be logged in to do this.");
                String message = request.queryParams("message");
                if(message.length() < 5 || message.length() > 250) return error("Message must be between 5 and 250 characters!");
                VisitorMessage vMessage = new VisitorMessage(-1, viewing.getId(), account.getId(), message, null, null);
                ForumConnection.connection().insert("visitor_messages", vMessage.data());
                prop.put("success", true);
                ArrayList<VisitorMessage> newMessages = ForumConnection.connection().selectList("visitor_messages", "account_id=?", "ORDER BY added DESC", VisitorMessage.class, viewing.getId());
                model.put("vmessages", newMessages);
                prop.put("html", WebModule.render("./source/modules/forums/user/visitor_messages.jade", model, request, response));
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
                    if(!forum.getPermissions().canCreateThread(account))
                        return error("You do not have permission to create threads in this forum.");
                    String title = request.queryParams("title");
                    String body = request.queryParams("body");
                    if (StringUtils.isNullOrEmpty(title) || title.length() < 5 || title.length() > 50)
                        return error("Title must be between 5 and 50 characters long.");
                    if (StringUtils.isNullOrEmpty(body) || body.length() < 5 || body.length() > 10_000)
                        return error("Body must be between 5 and 10,000 characters long.");
                    thread = new Thread(-1, forum.getId(), title, account.getId(), -1, -1, -1, null, false, -1, true, false, 0, false, null, null);
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
                    return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request, response);
                if (account == null)
                    return error("Not logged in. Please refresh the page and try again.");
                idString = request.params(":id");
                pageString = request.queryParams("page");
                try {
                    id = Integer.parseInt(idString);
                    page = Integer.parseInt(pageString);
                } catch (Exception e) {
                    return error("Error parsing id or page.");
                }
                thread = ForumConnection.connection().selectClass("threads", "id=?", Thread.class, id);
                if(thread == null) return error("Error finding thread. Please refresh and try again.");
                if(!thread.getSubForum().getPermissions().canReply(thread, account))
                    return error("You do not have permission to reply on this thread.");
                String body = request.queryParams("body");
                if (StringUtils.isNullOrEmpty(body) || body.length() < 5 || body.length() > 10_000)
                    return error("Body must be between 5 and 10,000 characters long.");
                Post post = new Post(-1, id, account.getId(), body, null, null, null);
                int insertId = ForumConnection.connection().insert("posts", post.data());
                post = ForumConnection.connection().selectClass("posts", "id=?", Post.class, insertId);
                thread.updateLastPost(post);
                HashMap<Integer, String> groups = Website.instance().getBBCodeManager().getBBCode(post.getPost(), "\\[USER=(.+?(?=\\]))]");
                if(groups != null) {
                    int userId = Integer.parseInt(groups.get(0));
                    Account user = GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, userId);
                    if(user != null)
                        NotificationManager.addNotification(user, "New Mention!", "Mentioned in "+thread.getTitle(), "fa fa-quote-left", "", "/forums/thread/"+thread.getId());
                }
                groups = Website.instance().getBBCodeManager().getBBCode(post.getPost(), "\\[QUOTE=(.+?(?=\\]))]");
                if(groups != null) {
                    int postId = Integer.parseInt(groups.get(0));
                    Post quoted = ForumConnection.connection().selectClass("posts", "id=?", Post.class, postId);
                    if(quoted != null)
                        NotificationManager.addNotification(quoted.getAuthor(), "Quoted!", "Quoted in "+thread.getTitle(), "fa fa-quote-left", "", "/forums/thread/"+thread.getId());
                }
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
            case "/forums/post/:id":
                idString = request.params(":id");
                try {
                    id = Integer.parseInt(idString);
                } catch (Exception e) {
                    return error("Error getting post. Please refresh page and try again.");
                }
                post = ForumConnection.connection().selectClass("posts", "id=?", Post.class, id);
                if (post == null)
                    return error("Error getting post. Please refresh page and try again.");
                if(!post.getThread().getSubForum().getPermissions().canReadThread(post.getThread(), account))
                    return error("You do not have permission to read this post.");
                model.put("post", post);
                prop.put("success", true);
                prop.put("html", render("./source/modules/forums/post.jade", model, request, response));
                break;
            case "/forums/post/:id/edit":
                idString = request.params(":id");
                try {
                    id = Integer.parseInt(idString);
                } catch (Exception e) {
                    return error("Error getting post. Please refresh page and try again.");
                }
                post = ForumConnection.connection().selectClass("posts", "id=?", Post.class, id);
                if(post == null) 
                    return error("Error getting post. Please refresh page and try again.");
                if(!post.getThread().getSubForum().getPermissions().canEdit(post, account))
                    return error("You do not have permission to edit this post.");
                model.put("post", post);
                prop.put("success", true);
                prop.put("html", render("./source/modules/forums/edit_post.jade", model, request, response));
                break;
            case "/forums/post/:id/submit-edit":
                idString = request.params(":id");
                try {
                    id = Integer.parseInt(idString);
                } catch (Exception e) {
                    return error("Error getting post. Please refresh page and try again.");
                }
                post = ForumConnection.connection().selectClass("posts", "id=?", Post.class, id);
                if (post == null)
                    return error("Error getting post. Please refresh page and try again.");
                if (!post.getThread().getSubForum().getPermissions().canEdit(post, account))
                    return error("You do not have permission to edit this post.");
                body = request.queryParams("post");
                if (StringUtils.isNullOrEmpty(body) || body.length() < 5 || body.length() > 10_000)
                    return error("Body must be between 5 and 10,000 characters long.");
                post.editPost(body);
                model.put("post", post);
                prop.put("success", true);
                prop.put("html", render("./source/modules/forums/post.jade", model, request, response));
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
            e.printStackTrace();
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
        if(!forum.getPermissions().canReadThread(thread, account))
            return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request, response);
        model.put("thread", thread);
        model.put("page", page);
        model.put("posts", thread.getPosts(page));
        ArrayList<String> crumbs = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();
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
            account.setStatus(-1, -1, -1, thread.getId());
        if(request.requestMethod().equals("GET"))
            return render("./source/modules/forums/forums.jade", model, request, response);
        thread.addView();
        prop.put("success", true);
        prop.put("html", render("./source/modules/forums/thread.jade", model, request, response));
        return Website.getGson().toJson(prop);
    }

    public String loadForum(Account account, int id, Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        Properties prop = new Properties();
        Object data = Website.instance().getCachingManager().getData("subforums-cache", id);
        if(data == null) return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request, response);
        SubForum forum = (SubForum) data;
        if(!forum.getPermissions().canSeeForum(account))
            return redirect("/forums", "Invalid request. Redirecting you back to home.", 5, null, request, response);
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
        prop.put("id", forum.getId());
        if(account != null)
            account.setStatus(-1, forum.getId(), -1, -1);
        if(request.requestMethod().equals("GET"))
            return render("./source/modules/forums/forums.jade", model, request, response);
        prop.put("success", true);
        prop.put("html", render("./source/modules/forums/forum.jade", model, request, response));
        return Website.getGson().toJson(prop);
    }

    @Override
    public Object decodeRequest(Request request, Response response, Website.RequestType type) {

        return null;
    }
}
