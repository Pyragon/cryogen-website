package com.cryo.modules.staff;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.PlayerReport;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class PlayerReports {

    @Endpoint(method = "POST", endpoint = "/staff/player-reports/load")
    public static String renderPlayerReportsPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/staff/player-reports", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Player Reports");
        model.put("module", "/staff/player-reports");
        model.put("moduleId", "player-reports");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all player reports made on Cryogen.");
            add("You can filter this list using the buttons on the right.");
            add("As staff, you have the ability to respond to these reports. Please make sure you read the staff section on the forums to ensure you respond up to Cryogen standard.");
            add("If you fail to respond up to Cryogen standard, you will be warned and you may lose your staff privileges.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/staff/player-reports/table")
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
        if(!request.queryParams().contains("page") || !NumberUtils.isDigits(request.queryParams("page")))
            return error("Unable to parse page number. Please refresh the page and try again.");
        int page = Integer.parseInt(request.queryParams("page"));

        ArrayList<Object> values = new ArrayList<>();
        String query = "archived "+(archived ? "IS NOT" : "IS")+" NULL";

        Object[] condition = ListManager.getCondition(filterValues, PlayerReport.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        int total = getConnection("cryogen_reports").selectCount("player_reports", query, values.toArray());
        String order = ListManager.getOrder(model, sortValues, PlayerReport.class, page, total, archived);
        List<PlayerReport> reports = getConnection("cryogen_reports").selectList("player_reports", query, order, PlayerReport.class, values.toArray());
        if(reports == null)
            return error("Error loading player reports. Please try again.");
        ListManager.buildTable(model, "staff", reports, PlayerReport.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }

}
