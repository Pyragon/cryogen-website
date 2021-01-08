package com.cryo.modules.account.support;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.PlayerReport;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class PlayerReports {

    @Endpoint(method = "POST", endpoint = "/support/player-reports/load")
    public static String renderPlayerReportsPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/support/player-reports", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("creatable", true);
        model.put("title", "Player Reports");
        model.put("module", "/support/player-reports");
        model.put("moduleId", "player-reports");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all player reports you have made against other players on Cryogen.");
            add("You can filter this list using the buttons on the right.");
            add("To create a new report, click 'New' on the right.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/support/player-reports/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("moduleId", "player-reports");
        ArrayList<ArrayList<Object>> sortValues = new ArrayList<>();
        if(request.queryParams().contains("sortValues"))
            sortValues = Website.getGson().fromJson(request.queryParams("sortValues"), ArrayList.class);
        ArrayList<ArrayList<Object>> filterValues = new ArrayList<>();
        if(request.queryParams().contains("filterValues"))
            filterValues = Website.getGson().fromJson(request.queryParams("filterValues"), ArrayList.class);
        boolean archived = Boolean.parseBoolean(request.queryParamOrDefault("archived", "false"));
        String order = ListManager.getOrder(sortValues, PlayerReport.class, archived);

        ArrayList<Object> values = new ArrayList<>();
        String query = "username=? AND archived "+(archived ? "IS NOT" : "IS")+" NULL";
        values.add(account.getUsername());

        Object[] condition = ListManager.getCondition(filterValues, PlayerReport.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        List<PlayerReport> reports = getConnection("cryogen_reports").selectList("player_reports", query, order, PlayerReport.class, values.toArray());
        if(reports == null)
            return error("Error loading player reports. Please try again.");
        ListManager.buildTable(model, "support", reports, PlayerReport.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }

}
