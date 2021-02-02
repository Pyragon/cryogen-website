package com.cryo.modules.account.support;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.PlayerReport;
import com.cryo.entities.accounts.support.ReportVerification;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.cryo.utils.DisplayNames;
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
            add("However, it is HIGHLY recommended you begin a report by right clicking the player in-game to ensure the name is correct.");
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
        if(!request.queryParams().contains("page") || !NumberUtils.isDigits(request.queryParams("page")))
            return error("Unable to parse page number. Please refresh the page and try again.");
        int page = Integer.parseInt(request.queryParams("page"));

        ArrayList<Object> values = new ArrayList<>();
        String query = "username=? AND archived "+(archived ? "IS NOT" : "IS")+" NULL";
        values.add(account.getUsername());

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
        ListManager.buildTable(model, "support", reports, PlayerReport.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/player-reports/new")
    public static String newReport(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        return renderPage("account/support/player-reports/new-report", null, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/player-reports/view")
    public static String viewReport(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse id. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        PlayerReport report = getConnection("cryogen_reports").selectClass("player_reports", "id=?", PlayerReport.class, id);
        if(report == null || !report.getUsername().equals(account.getUsername()))
            return error("Unable to find report. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("report", report);
        return renderPage("account/support/player-reports/view-report", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/player-reports/submit-verify")
    public static String newReportFromServer(Request request, Response response) {
        //create verification id, etc, return to server
        //server can then open up a GET request to /support/player-reports, we'll add special condition to open up new report noty right away
        //TODO
        return "";
    }

    @Endpoint(method = "POST", endpoint = "/support/player-reports/submit")
    public static String submitReport(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        int verifyId = -1;
        String username = request.queryParams("offender");
        if(username.equals(account.getUsername()))
            return error("You cannot report yourself.");
        if(request.queryParams().contains("verifyId") && !request.queryParams("verifyId").equals("")) {
            if(!NumberUtils.isDigits(request.queryParams("verifyId")))
                return error("Unable to parse verification id. Please refresh the page and try again. If you were reporting from in-game. You will have to restart the report from in-game again.");
            verifyId = Integer.parseInt(request.queryParams("verifyId"));
            ReportVerification verify = getConnection("cryogen_reports").selectClass("verified_names", "verify_id=?", ReportVerification.class, verifyId);
            if(verify == null)
                return error("Unable to find verification. Please refresh the page and try again. If you were reporting from in-game. You will have to restart the report from in-game again.");
            if(!verify.getUsername().equals(username))
                return error("Usernames do not match between verification. Please do not change the username at all after reporting from in-game. You will have to restart the report from in-game again.");
            if(verify.getExpiry().getTime() < System.currentTimeMillis())
                return error("Verification has expired. Please restart the report from in-game again.");
        }
        String title = request.queryParams("title");
        String rule = request.queryParams("rule");
        String date = request.queryParams("date");
        String proof = request.queryParams("proof");
        String additional = request.queryParams("additional");
        if(Utilities.isNullOrEmpty(title, rule, date, proof, additional))
            return error("All fields must be filled out.");
        if(title.length() < 5 || title.length() > 20)
            return error("Title must be between 5 and 20 characters.");
        if(rule.length() < 3 || rule.length() > 30)
            return error("Rule must be between 3 and 30 characters.");
        if(proof.length() < 10 || proof.length() > 200)
            return error("Proof must be between 10 and 200 characters.");
        if(additional.length() < 5 || additional.length() > 200)
            return error("Additional information must be between 5 and 200 characters.");
        Timestamp stamp;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            stamp = new Timestamp(format.parse(date).getTime());
        } catch(Exception e) {
            e.printStackTrace();
            return error("Error parsing. Please check the date and try again. Ensure it is in yyyy-MM-dd format");
        }
        Account offender = AccountUtils.getAccount(Utilities.formatNameForProtocol(username));
        if(offender == null) {
            username = DisplayNames.getUsername(username);
            offender = AccountUtils.getAccount(username);
            if(offender == null)
                return error("Unable to find that player. Please check the name and try again. You may use either their username or current display name.");
        }
        PlayerReport report = new PlayerReport(-1, account.getUsername(), -1, title, offender.getUsername(), (verifyId != -1), rule, additional, proof, stamp, null, null, null, null);
        getConnection("cryogen_reports").insert("player_reports", report.data());
        if(verifyId != -1)
            getConnection("cryogen_reports").delete("verified_names", "verify_id=?", verifyId);
        return success();
    }

}
