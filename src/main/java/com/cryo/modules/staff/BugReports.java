package com.cryo.modules.staff;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.BugReport;
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
public class BugReports {

    @Endpoint(method = "POST", endpoint = "/staff/bug-reports/load")
    public static String renderPlayerReportsPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/staff/bug-reports", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Bug Reports");
        model.put("module", "/staff/bug-reports");
        model.put("moduleId", "bug-reports");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all the bug reports made in Cryogen.");
            add("You can filter this list using the buttons on the right.");
            add("As staff, you can respond to these reports. But please only respond if you know what you are doing.");
            add("Responses made by anyone other than Cody should on be about explaining a possible fix, or that the bug has already been reported.");
            add("If the bug is website related, and the bug is not yet on Github. I ask staff to please create the issue on Github, and then archive the report with a link to the Github issue.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/staff/bug-reports/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("moduleId", "bug-reports");
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

        Object[] condition = ListManager.getCondition(filterValues, BugReport.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        int total = getConnection("cryogen_reports").selectCount("bug_reports", query, values.toArray());
        String order = ListManager.getOrder(model, sortValues, BugReport.class, page, total, archived);
        List<BugReport> reports = getConnection("cryogen_reports").selectList("bug_reports", query, order, BugReport.class, values.toArray());
        if(reports == null)
            return error("Error loading player reports. Please try again.");
        ListManager.buildTable(model, "staff", reports, BugReport.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }
}
