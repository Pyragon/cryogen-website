package com.cryo.modules.staff;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.Appeal;
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
public class Appeals {

    @Endpoint(method = "POST", endpoint = "/staff/appeals/load")
    public static String renderAppealsPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/staff/appeals", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Appeals");
        model.put("module", "/staff/appeals");
        model.put("moduleId", "appeals");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all appeals made by players on Cryogen.");
            add("You can filter this list using the buttons on the right.");
            add("Please completely read the forums before making any decisions on appeals.");
            add("Please also consult the punisher, and keep the recommended punishment time in mind when making any decisions.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/staff/appeals/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("moduleId", "appeals");
        ArrayList<ArrayList<Object>> sortValues = new ArrayList<>();
        if(request.queryParams().contains("sortValues"))
            sortValues = Website.getGson().fromJson(request.queryParams("sortValues"), ArrayList.class);
        ArrayList<ArrayList<Object>> filterValues = new ArrayList<>();
        if(request.queryParams().contains("filterValues"))
            filterValues = Website.getGson().fromJson(request.queryParams("filterValues"), ArrayList.class);
        boolean archived = Boolean.parseBoolean(request.queryParamOrDefault("archived", "false"));
        String order = ListManager.getOrder(sortValues, Appeal.class, archived);

        ArrayList<Object> values = new ArrayList<>();
        String query = "answered "+(archived ? "IS NOT" : "IS")+" NULL";

        Object[] condition = ListManager.getCondition(filterValues, Appeal.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        List<Appeal> appeals = getConnection("cryogen_punish").selectList("appeals", query, order, Appeal.class, values.toArray());
        if(appeals == null)
            return error("Error loading player reports. Please try again.");
        ListManager.buildTable(model, "staff", appeals, Appeal.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }
}
