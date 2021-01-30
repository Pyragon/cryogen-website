package com.cryo.modules.account.support;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.Appeal;
import com.cryo.entities.accounts.support.BugReport;
import com.cryo.entities.accounts.support.Punishment;
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
public class Appeals {

    @Endpoint(method = "POST", endpoint = "/support/appeals/load")
    public static String renderAppealsPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/support/appeals", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Appeals");
        model.put("module", "/support/appeals");
        model.put("moduleId", "appeals");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all appeals you have made for punishments received in Cryogen.");
            add("You can filter this list using the buttons on the right.");
            add("To create an appeal, use the button on the punishment in the 'Punishments' tab. Not all punishments can be appealed.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/support/appeals/table")
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
        if(!request.queryParams().contains("page") || !NumberUtils.isDigits(request.queryParams("page")))
            return error("Unable to parse page number. Please refresh the page and try again.");
        int page = Integer.parseInt(request.queryParams("page"));

        ArrayList<Object> values = new ArrayList<>();
        String query = "username=? AND archived "+(archived ? "IS NOT" : "IS")+" NULL";
        values.add(account.getUsername());

        Object[] condition = ListManager.getCondition(filterValues, Appeal.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        int total = getConnection("cryogen_punish").selectCount("appeals", query, values.toArray());
        String order = ListManager.getOrder(model, sortValues, Appeal.class, page, total, archived);
        List<Appeal> appeals = getConnection("cryogen_punish").selectList("appeals", query, order, Appeal.class, values.toArray());
        if(appeals == null)
            return error("Error loading player reports. Please try again.");
        ListManager.buildTable(model, "account", appeals, Appeal.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/appeals/view")
    public static String viewAppeal(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse ID. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        if(request.queryParams().contains("punishment")) {
            Punishment punishment = getConnection("cryogen_punish").selectClass("punishments", "id=?", Punishment.class, id);
            if(punishment == null)
                return error("Unable to find appeal. Please refresh the page and try again.");
            id = punishment.getAppealId();
        }
        Appeal appeal = getConnection("cryogen_punish").selectClass("appeals", "id=?", Appeal.class, id);
        if(appeal == null || !appeal.getUsername().equalsIgnoreCase(account.getUsername()))
            return error("Unable to find appeal. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("appeal", appeal);
        return renderPage("account/support/appeals/view-appeal", model, request, response);
    }

    public static String respond(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse ID. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        Appeal appeal = getConnection("cryogen_punish").selectClass("appeals", "id=?", Appeal.class, id);
        if(appeal == null || !appeal.getUsername().equalsIgnoreCase(account.getUsername()))
            return error("Unable to find appeal. Please refresh the page and try again.");
        if(appeal.getPunishment().getType() == 1 && account.getRights() == 1)
            return error("Only admins may respond to ban appeals.");
        String answer = request.queryParams("answer");
        //answer -> 0/1
        //set into database
        //send notification to initial punisher if it's not us.
        return error("Test");
    }
}
