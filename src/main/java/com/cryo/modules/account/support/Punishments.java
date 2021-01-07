package com.cryo.modules.account.support;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.BugReport;
import com.cryo.entities.accounts.support.Punishment;
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
public class Punishments {

    @Endpoint(method = "POST", endpoint = "/support/punishments/load")
    public static String renderPunishmentsPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/support/punishments", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Punishments");
        model.put("module", "/support/punishments");
        model.put("moduleId", "punishments");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all of the punishments you have received on Cryogen.");
            add("If the punishment is appealable, you can attempt an appeal by clicking the appropriate button.");
            add("Please do not PM your punisher to try and appeal. It will not quicken your appeal.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/support/punishments/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("moduleId", "punishments");
        ArrayList<ArrayList<Object>> sortValues = new ArrayList<>();
        if(request.queryParams().contains("sortValues"))
            sortValues = Website.getGson().fromJson(request.queryParams("sortValues"), ArrayList.class);
        ArrayList<ArrayList<Object>> filterValues = new ArrayList<>();
        if(request.queryParams().contains("filterValues"))
            filterValues = Website.getGson().fromJson(request.queryParams("filterValues"), ArrayList.class);
        boolean archived = Boolean.parseBoolean(request.queryParamOrDefault("archived", "false"));
        String order = ListManager.getOrder(sortValues, Punishment.class, archived);

        ArrayList<Object> values = new ArrayList<>();
        String query = "username=? AND archived "+(archived ? "IS NOT" : "IS")+" NULL";
        values.add(account.getUsername());

        Object[] condition = ListManager.getCondition(filterValues, Punishment.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        List<Punishment> punishments = getConnection("cryogen_punish").selectList("punishments", query, order, Punishment.class, values.toArray());
        if(punishments == null)
            return error("Error loading player reports. Please try again.");
        ListManager.buildTable(model, punishments, Punishment.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }
}
