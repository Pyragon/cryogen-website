package com.cryo.modules.logs;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.logs.Death;
import com.cryo.entities.logs.Dice;
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
public class Dices {

    @Endpoint(method = "POST", endpoint = "/logs/dice/load")
    public static String renderDeathLogs(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return com.cryo.modules.account.Login.renderLoginPage("/logs/dice", request, response);
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Dice Logs");
        model.put("module", "/logs/dice");
        model.put("moduleId", "dice");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all in-game dice matches between players on Cryogen.");
            add("You can filter this list using the buttons on the right.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/logs/dice/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("moduleId", "dice");
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

        Object[] condition = ListManager.getCondition(filterValues, Death.class, false);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        int total = getConnection("cryogen_logs").selectCount("dice", query, values.toArray());
        String order = ListManager.getOrder(model, sortValues, Dice.class, page, total, false);
        List<Dice> dices = getConnection("cryogen_logs").selectList("dice", query, order, Dice.class, values.toArray());
        if(dices == null)
            return error("Error loading dices. Please try again.");
        ListManager.buildTable(model, "logs", dices, Dice.class, account, sortValues, filterValues, false);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/logs/dice/view")
    public static String viewTrade(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse id. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        Dice dice = getConnection("cryogen_logs").selectClass("dice", "id=?", Dice.class, id);
        if(dice == null)
            return error("Unable to find trade. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("dice", dice);
        return renderPage("logs/views/death", model, request, response);
    }
}
