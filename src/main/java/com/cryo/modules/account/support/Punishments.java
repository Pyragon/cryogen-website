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
        if(!request.queryParams().contains("page") || !NumberUtils.isDigits(request.queryParams("page")))
            return error("Unable to parse page number. Please refresh the page and try again.");
        int page = Integer.parseInt(request.queryParams("page"));

        ArrayList<Object> values = new ArrayList<>();
        String query;
        if(archived)
            query = "username=? AND (archived IS NOT NULL OR (expiry IS NOT NULL AND expiry < NOW()))";
        else
            query = "username=? AND (archived IS NULL AND (expiry IS NULL OR expiry > NOW()))";
        values.add(account.getUsername());

        Object[] condition = ListManager.getCondition(filterValues, Punishment.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        int total = getConnection("cryogen_punish").selectCount("punishments", query, values.toArray());
        String order = ListManager.getOrder(model, sortValues, Punishment.class, page, total, archived);
        List<Punishment> punishments = getConnection("cryogen_punish").selectList("punishments", query, order, Punishment.class, values.toArray());
        if(punishments == null)
            return error("Error loading player reports. Please try again.");
        ListManager.buildTable(model, "account", punishments, Punishment.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/punishments/view")
    public static String viewPunishment(Request request, Response response) {
        Object obj = getPunishment(request);
        if(obj instanceof String)
            return (String) obj;
        Punishment punishment = (Punishment) obj;
        HashMap<String, Object> model = new HashMap<>();
        model.put("punishment", punishment);
        return renderPage("account/support/punishments/view-punishment", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/punishments/create-appeal")
    public static String createAppeal(Request request, Response response) {
        Object obj = getPunishment(request);
        if(obj instanceof String)
            return (String) obj;
        Punishment punishment = (Punishment) obj;
        if(punishment.getAppealId() > 0)
            return error("An appeal for this punishment already exists. Please refresh the page and try again.");
        if(!punishment.isAppealable())
            return error("This punishment is not appealable. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("punishment", punishment);
        return renderPage("account/support/appeals/create-appeal", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/support/punishments/submit-appeal")
    public static String submitAppeal(Request request, Response response) {
        Object obj = getPunishment(request);
        if(obj instanceof String)
            return (String) obj;
        Punishment punishment = (Punishment) obj;
        if(punishment.getAppealId() > 0)
            return error("An appeal for this punishment already exists. Please refresh the page and try again.");
        if(!punishment.isAppealable())
            return error("This punishment is not appealable. Please refresh the page and try again.");
        Account account = AccountUtils.getAccount(request);
        String title = request.queryParams("title");
        String additional = request.queryParams("additional");
        if(Utilities.isNullOrEmpty(title, additional))
            return error("All fields must be filled out.");
        if(title.length() < 5 || additional.length() < 5)
            return error("Both the title and the additional information must be at least 5 characters.");
        Appeal appeal = new Appeal(-1, account.getUsername(), punishment.getId(), title, additional, null, null, -1, null, request.ip(), null, null);
        int appealId =  getConnection("cryogen_punish").insert("appeals", appeal.data());
        getConnection("cryogen_punish").set("punishments", "appeal_id=?", "id=?", appealId, punishment.getId());
        return success();
    }

    public static Object getPunishment(Request request) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse ID. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        Punishment punishment = getConnection("cryogen_punish").selectClass("punishments", "id=?", Punishment.class, id);
        if(punishment == null || !punishment.getUsername().equals(account.getUsername()))
            return error("Unable to find punishment. Please refresh the page and try again.");
        return punishment;
    }
}
