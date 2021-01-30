package com.cryo.modules.account.support;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.BugReport;
import com.cryo.entities.accounts.support.PlayerReport;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.cryo.utils.Utilities;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class BugReports {

    @Endpoint(method = "POST", endpoint = "/support/bug-reports/load")
    public static String renderPlayerReportsPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/support/bug-reports", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("creatable", true);
        model.put("title", "Bug Reports");
        model.put("module", "/support/bug-reports");
        model.put("moduleId", "bug-reports");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all reports you have made for bugs encountered in Cryogen.");
            add("You can filter this list using the buttons on the right.");
            add("To create a new report, click 'New' on the right.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/support/bug-reports/table")
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
        String query = "username=? AND archived "+(archived ? "IS NOT" : "IS")+" NULL";
        values.add(account.getUsername());

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
        ListManager.buildTable(model, "account", reports, BugReport.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/bug-reports/view")
    public static String viewReport(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse id. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        BugReport report = getConnection("cryogen_reports").selectClass("bug_reports", "id=?", BugReport.class, id);
        if(report == null || !report.getUsername().equals(account.getUsername()))
            return error("Unable to find report. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("report", report);
        return renderPage("account/support/bug-reports/view-report", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/bug-reports/create")
    public static String createReport(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        return renderPage("account/support/bug-reports/create-report", null, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/bug-reports/submit")
    public static String submitReport(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        String title = request.queryParams("title");
        String replicate = request.queryParams("replicate");
        String date = request.queryParams("date");
        String additional = request.queryParams("additional");
        if(Utilities.isNullOrEmpty(title, additional))
            return error("All fields must be filled out.");
        if (title.length() < 5 || additional.length() < 5)
            return error("Both title and additional information must be at least 5 characters.");
        if(!replicate.equals("true") && !replicate.equals("false"))
            return error("Invalid replicate value. Please refresh the page and try again.");
        if(!request.queryParams().contains("type") || !NumberUtils.isDigits(request.queryParams("type")))
            return error("Invalid type value. Please refresh the page and try again.");
        int type = Integer.parseInt(request.queryParams("type"));
        if(type != 0 && type != 1)
            return error("Invalid type value. Please refresh the page and try again.");
        boolean replicateable = Boolean.parseBoolean(replicate);
        Timestamp seen;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            seen = new Timestamp(format.parse(date).getTime());
        } catch(Exception e) {
            return error("Invalid date. Please try again. Ensure the date is in yyyy-MM-dd format.");
        }
        BugReport report = new BugReport(-1, account.getUsername(), title, type, replicateable, seen, additional, null, null, null, null);
        getConnection("cryogen_reports").insert("bug_reports", report.data());
        return success();
    }

}
