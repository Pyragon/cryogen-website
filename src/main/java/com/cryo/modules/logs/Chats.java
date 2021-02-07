package com.cryo.modules.logs;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.list.CustomAction;
import com.cryo.entities.logs.Chat;
import com.cryo.entities.logs.Death;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.utils.Utilities;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class Chats {

    @Endpoint(method = "POST", endpoint = "/logs/chat/load")
    public static String renderChatLogs(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return com.cryo.modules.account.Login.renderLoginPage("/logs/chat", request, response);
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Chat Logs");
        model.put("module", "/logs/chat");
        model.put("moduleId", "chat");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all in-game chat messages by players on Cryogen.");
            add("You can filter this list using the buttons on the right.");
        }});
        model.put("customActions", new ArrayList<CustomAction>() {{
            add(new CustomAction("Download", "download-chat", "fa-save", true));
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/logs/chat/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("moduleId", "chat");
        ArrayList<ArrayList<Object>> sortValues = new ArrayList<>();
        if(request.queryParams().contains("sortValues"))
            sortValues = Website.getGson().fromJson(request.queryParams("sortValues"), ArrayList.class);
        ArrayList<ArrayList<Object>> filterValues = new ArrayList<>();
        if(request.queryParams().contains("filterValues"))
            filterValues = Website.getGson().fromJson(request.queryParams("filterValues"), ArrayList.class);
        if(!request.queryParams().contains("page") || !NumberUtils.isDigits(request.queryParams("page")))
            return error("Unable to parse page number. Please refresh the page and try again.");
        int page = Integer.parseInt(request.queryParams("page"));

        ArrayList<Object> values = new ArrayList<>();
        String query = "";

        Object[] condition = ListManager.getCondition(filterValues, Chat.class, false);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        int total = getConnection("cryogen_logs").selectCount("chat", query, values.toArray());
        String order = ListManager.getOrder(model, sortValues, Chat.class, page, total, false);
        List<Chat> chats = getConnection("cryogen_logs").selectList("chat", query, order, Chat.class, values.toArray());
        if(chats == null)
            return error("Error loading chats. Please try again.");
        ListManager.buildTable(model, "logs", chats, Chat.class, account, sortValues, filterValues, false);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/logs/chat/download")
    public static String downloadChatLogs(Request request, Response response) {
        return "";
    }
}
