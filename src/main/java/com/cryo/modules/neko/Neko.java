package com.cryo.modules.neko;

import com.cryo.Website;
import com.cryo.entities.MiscData;
import com.cryo.entities.MovieNightNowPlaying;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoint;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.cryo.utils.Utilities;
import com.google.gson.internal.LinkedTreeMap;
import com.mysql.cj.util.StringUtils;
import spark.Request;
import spark.Response;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class Neko {

    @SPAEndpoint("/neko")
    public static String renderMovieNightPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if (account == null)
            return Login.renderLoginPage("/neko", request, response);
        String sessionId = request.cookie("cryo_sess");
        if (sessionId == null)
            sessionId = request.session().attribute("cryo_sess");
        HashMap<String, Object> model = new HashMap<>();
        model.put("sessionId", sessionId);
        MiscData data = getConnection("cryogen_global").selectClass("misc_data", "name=?", MiscData.class, "movie_night");
        MovieNightNowPlaying now = new MovieNightNowPlaying("N/A", "movie");
        if(data != null)
            now = Website.getGson().fromJson(data.getValue(), MovieNightNowPlaying.class);
        model.put("now", now);
        return renderPage("neko/index", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/neko/member-list")
    public static String renderMemberList(Request request, Response response) {
        if (AccountUtils.getAccount(request) == null)
            return Login.renderLoginPage("/neko", request, response);
        if (!request.queryParams().contains("members"))
            return error("Unable to parse members. Please refresh the page and try again.");
        String membersStr = request.queryParams("members");
        ArrayList<LinkedTreeMap<String, Object>> members;
        try {
            members = Website.getGson().fromJson(membersStr, ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
            return error("Unable to parse members. Please refresh the page and try again.");
        }
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Account> accounts = new ArrayList<>();
        for (LinkedTreeMap<String, Object> map : members) {
            Account account = AccountUtils.getAccount((String) map.get("displayname"));
            if (account == null) continue;
            accounts.add(account);
        }
        model.put("members", accounts);
        return renderPage("neko/member-list", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/neko/chat")
    public static String renderChat(Request request, Response response) {
        if (AccountUtils.getAccount(request) == null)
            return Login.renderLoginPage("/neko", request, response);
        if (!request.queryParams().contains("messages"))
            return error("Unable to parse messages. Please refresh the page and try again.");
        String messagesStr = request.queryParams("messages");
        ArrayList<LinkedTreeMap<String, Object>> messages;
        try {
            messages = Website.getGson().fromJson(messagesStr, ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
            return error("Unable to parse messages. Please refresh the page and try again.");
        }
        HashMap<String, Object> model = new HashMap<>();
        ArrayList<Properties> results = new ArrayList<>();
        messages.forEach(m -> {
            Account account = AccountUtils.getAccount((String) m.get("username"));
            if (account == null) return;
            Properties prop = new Properties();
            prop.put("id", m.get("id"));
            prop.put("content", m.get("content"));
            prop.put("stamp", new Timestamp((long) (double) m.get("stamp")));
            prop.put("account", account);
            results.add(prop);
        });
        model.put("messages", results);
        return renderPage("neko/chat", model, request, response);
    }

    @Endpoint(values = {"POST", "/neko/mute", "POST", "/neko/unmute"})
    public static String muteOrUnmuteUser(String endpoint, Request request, Response response) {
        Account account;
        if ((account = AccountUtils.getAccount(request)) == null)
            return error("Invalid login. Please refresh the page and try again.");
        if (account.getRights() < 1)
            return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        if (!request.queryParams().contains("target"))
            return error("No target specified. Please refresh the page and try again.");
        String targetName = request.queryParams("target");
        Account target = AccountUtils.getAccount(targetName);
        if (target == null)
            return error("Unable to find target. Please refresh the page and try again.");
        if (target.getRights() > 0)
            return error("You cannot mute other staff.");
        String action = endpoint.replace("/neko/", "");
        if (!action.equals("mute") && !action.equals("unmute"))
            return error("Invalid action. Please refresh the page and try again.");
        if (action.equals("mute") && target.isMutedFromMovieNight())
            return error("Target is already muted from movie night. Please refresh the page and try again.");
        else if (action.equals("unmute") && !target.isMutedFromMovieNight())
            return error("Target is not muted from movie night. Please refresh the page and try again.");
        getConnection("cryogen_global").set("player_data", "muted_from_movie_night=?", "id=?", action.equals("mute") ? 1 : 0, target.getId());
        return success();
    }

    @Endpoint(method = "POST", endpoint = "/neko/ban")
    public static String banUser(String endpoint, Request request, Response response) {
        Account account;
        if ((account = AccountUtils.getAccount(request)) == null)
            return error("Invalid login. Please refresh the page and try again.");
        if (account.getRights() < 2)
            return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        if (!request.queryParams().contains("target"))
            return error("No target specified. Please refresh the page and try again.");
        String targetName = request.queryParams("target");
        Account target = AccountUtils.getAccount(targetName);
        if (target == null)
            return error("Unable to find target. Please refresh the page and try again.");
        if (target.getRights() > 0)
            return error("You cannot ban other staff.");
        String action = endpoint.replace("/neko/", "");
        if (!action.equals("ban") && !action.equals("unban"))
            return error("Invalid action. Please refresh the page and try again.");
        if (action.equals("ban") && target.isBannedFromMovieNight())
            return error("Target is already banned from movie night. Please refresh the page and try again.");
        else if (action.equals("unban") && !target.isBannedFromMovieNight())
            return error("Target is not banned from movie night. Please refresh the page and try again.");
        getConnection("cryogen_global").set("player_data", "banned_from_movie_night=?", "id=?", action.equals("ban") ? 1 : 0, target.getId());
        return success();
    }

    @Endpoint(method = "POST", endpoint = "/neko/now-playing")
    public static String renderNowPlaying(Request request, Response response) {
        if (AccountUtils.getAccount(request) == null)
            return error("Invalid login. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        MiscData data = getConnection("cryogen_global").selectClass("misc_data", "name=?", MiscData.class, "movie_night");
        MovieNightNowPlaying now = new MovieNightNowPlaying("N/A", "movie");
        if(data != null)
            now = Website.getGson().fromJson(data.getValue(), MovieNightNowPlaying.class);
        model.put("now", now);
        return renderPage("neko/now-playing", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/neko/now-playing/edit")
    public static String editNowPlaying(Request request, Response response) {
        Account account;
        if ((account = AccountUtils.getAccount(request)) == null)
            return error("Invalid login. Please refresh the page and try again.");
        if (account.getRights() < 2)
            return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        MiscData data = getConnection("cryogen_global").selectClass("misc_data", "name=?", MiscData.class, "movie_night");
        MovieNightNowPlaying now = new MovieNightNowPlaying("N/A", "movie");
        if(data != null)
            now = Website.getGson().fromJson(data.getValue(), MovieNightNowPlaying.class);
        model.put("now", now);
        return renderPage("neko/edit-now-playing", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/neko/now-playing/submit")
    public static String submitNowPlaying(Request request, Response response) {
        Account account;
        if ((account = AccountUtils.getAccount(request)) == null)
            return error("Invalid login. Please refresh the page and try again.");
        if (account.getRights() < 2)
            return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        String title = request.queryParams("title");
        if(StringUtils.isNullOrEmpty(title) || title.length() < 5 || title.length() > 20)
            return error("Title must be between 5 and 20 characters.");
        String type = request.queryParams("type");
        if(type == null || (!type.equals("movie") && !type.equals("show") && !type.equals("misc")))
            return error("Invalid type. Please refresh the page and try again.");
        MovieNightNowPlaying np = new MovieNightNowPlaying(title, type);
        if(type.equals("show")) {
            String show = request.queryParams("show");
            if(show == null || show.length() < 4 || show.length() > 20)
                return error("Show must be between 4 and 20 characters.");
            String seasonEpisode = request.queryParams("seasonEpisode");
            if(seasonEpisode == null || seasonEpisode.length() < 3 || seasonEpisode.length() > 20)
                return error("Season/Episode must be between 3 and 20 characters.");
            np.setShow(show);
            np.setSeasonEpisode(seasonEpisode);
        }
        if(getConnection("cryogen_global").selectCount("misc_data", "name=?", "movie_night") > 0)
            getConnection("cryogen_global").set("misc_data", "value=?", "name=?", Website.getGson().toJson(np), "movie_night");
        else
            getConnection("cryogen_global").insert("misc_data", "DEFAULT", "movie_night", Website.getGson().toJson(np), "DEFAULT", "DEFAULT");
        return success();
    }

}
