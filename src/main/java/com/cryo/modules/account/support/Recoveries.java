package com.cryo.modules.account.support;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.Recovery;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
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
public class Recoveries {

    @Endpoint(method = "POST", endpoint = "/support/recoveries/load")
    public static String renderRecoveriesPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/support/recoveries", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Recoveries");
        model.put("module", "/support/recoveries");
        model.put("moduleId", "recoveries");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all recoveries made by you for your account.");
            add("You can filter this list using the buttons on the right.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/recoveries/table")
    public static String renderTable(Request request, Response response) {
        try {
            Account account = AccountUtils.getAccount(request);
            if (account == null) return error("Session has expired. Please refresh the page and try again.");
            HashMap<String, Object> model = new HashMap<>();
            model.put("moduleId", "recoveries");
            ArrayList<ArrayList<Object>> sortValues = new ArrayList<>();
            if (request.queryParams().contains("sortValues"))
                sortValues = Website.getGson().fromJson(request.queryParams("sortValues"), ArrayList.class);
            ArrayList<ArrayList<Object>> filterValues = new ArrayList<>();
            if (request.queryParams().contains("filterValues"))
                filterValues = Website.getGson().fromJson(request.queryParams("filterValues"), ArrayList.class);
            if (!request.queryParams().contains("page") || !NumberUtils.isDigits(request.queryParams("page")))
                return error("Unable to parse page number. Please refresh the page and try again.");
            int page = Integer.parseInt(request.queryParams("page"));

            ArrayList<Object> values = new ArrayList<>();
            String query = "username=? AND status != 1";

            values.add(account.getUsername());

            Object[] condition = ListManager.getCondition(filterValues, Recovery.class, true);
            if (condition.length == 2 && condition[0] != null) {
                query += (String) condition[0];
                values.addAll((ArrayList<Object>) condition[1]);
            }
            int total = getConnection("cryogen_recovery").selectCount("recoveries", query, values.toArray());
            String order = ListManager.getOrder(model, sortValues, Recovery.class, page, total, true);
            List<Recovery> recoveries = getConnection("cryogen_recovery").selectList("recoveries", query, order, Recovery.class, values.toArray());
            if (recoveries == null)
                return error("Error loading recoveries. Please try again.");
            model.put("archived", true);
            ListManager.buildTable(model, "support", recoveries, Recovery.class, account, sortValues, filterValues, true);
            return renderList(model, request, response);
        } catch(Exception e) {
            e.printStackTrace();
            return error("Error loading recoveries table. Please try again later.");
        }
    }
}
