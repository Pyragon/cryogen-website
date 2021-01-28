package com.cryo.modules.staff;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.Appeal;
import com.cryo.entities.accounts.support.Punishment;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.cryo.utils.DisplayNames;
import com.cryo.utils.Utilities;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class Punishments {

    @Endpoint(method = "POST", endpoint = "/staff/punishments/load")
    public static String renderAppealsPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/staff/punishments", request, response);
        if(account.getRights() < 1) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("creatable", true);
        model.put("title", "Punishments");
        model.put("module", "/staff/punishments");
        model.put("moduleId", "punishments");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all punishments against players in Cryogen.");
            add("You can filter this list using the buttons on the right.");
            add("You can reverse a punishment through this page, through it is highly recommended you only do so through the appeals section instead.");
            add("Reversing a punishment through this page should only be done if the a staff member feels the punishment is unfair or unjust.");
            add("Please read all threads on the forums pertaining to this section to avoid being demoted.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/staff/punishments/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 1) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
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
            query = "archived IS NOT NULL OR (expiry IS NOT NULL AND expiry < NOW())";
        else
            query = "archived IS NULL AND (expiry IS NULL OR expiry > NOW())";

        Object[] condition = ListManager.getCondition(filterValues, Punishment.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        int total = getConnection("cryogen_punish").selectCount("punishments", query, values.toArray());
        String order = ListManager.getOrder(model, sortValues, Punishment.class, page, total, archived);
        List<Punishment> appeals = getConnection("cryogen_punish").selectList("punishments", query, order, Punishment.class, values.toArray());
        if(appeals == null)
            return error("Error loading player reports. Please try again.");
        model.put("archived", archived);
        ListManager.buildTable(model, "staff", appeals, Punishment.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/staff/punishments/new")
    public static String createNewPunishment(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 1) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        return renderPage("staff/punishments/new-punishment", null, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/staff/punishments/submit")
    public static String submitPunishment(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 1) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        String name = request.queryParams("name");
        String type = request.queryParams("type");
        String reason = request.queryParams("reason");
        String expiryS = request.queryParams("expiry");
        String appealableS = request.queryParams("appealable");
        String info = request.queryParams("info");
        if(Utilities.isNullOrEmpty(name, type, reason, info))
            return error("All fields other than expiry must be filled out.");
        if(info.length() < 5)
            return error("Info field must be at least 5 characters.");
        String username = DisplayNames.getUsername(name);
        Account target = AccountUtils.getAccount(username);
        if(target == null)
            return error("Unable to find that user. Please check the name and try again.");
        if(!type.equals("mute") && !type.equals("ban"))
            return error("Invalid type. Please refresh the page and try again.");
        if(type.equals("ban") && account.getRights() == 1)
            return error("You must be an administrator to ban accounts. Please contact an admin.");
        if(appealableS == null || StringUtils.isNullOrEmpty(appealableS))
            return error("Invalid appeal value. Please refresh the page and try again.");
        boolean appealable = Boolean.parseBoolean(appealableS);
        Timestamp expiry = null;
        if(expiryS != null && !expiryS.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            try {
                expiry = new Timestamp(format.parse(expiryS).getTime());
            } catch(Exception e) {
                e.printStackTrace();
                return error("Invalid expiry date provided. Please check the date and try again.");
            }
        }
        Punishment punishment = new Punishment(-1, -1, -1, username, type.equals("mute") ? 0 : 1, appealable, expiry, account.getUsername(), reason, info, null, null, null, null);
        getConnection("cryogen_punish").insert("punishments", punishment.data());
        return success();
    }

    @Endpoint(method = "POST", endpoint = "/staff/punishments/reverse")
    public static String reversePunishment(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 1) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Invalid ID. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        Punishment punishment = getConnection("cryogen_punish").selectClass("punishments", "id=?", Punishment.class, id);
        if(punishment == null)
            return error("Unable to find punishment. Please refresh the page and try again.");
        if(punishment.isArchived())
            return error("Punishment is not currently active. Please refresh the page and try again.");
        if(punishment.getType() == 1 && account.getRights() == 1)
            return error("You must be an admin to reverse bans.");
        getConnection("cryogen_punish").set("punishments", "archived=NOW(), archiver=?", "id=?", account.getUsername(), punishment.getId());
        return success();
    }

    @Endpoint(method = "POST", endpoint = "/staff/punishments/view")
    public static String viewPunishment(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 1) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse ID. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        Punishment punishment = getConnection("cryogen_punish").selectClass("punishments", "id=?", Punishment.class, id);
        if(punishment == null)
            return error("Unable to find punishment. Please refresh the page and try again.");
        model.put("punishment", punishment);
        Account user = AccountUtils.getAccount(punishment.getUsername());
        if(user == null)
            return error("Unable to find user punishment is based on. Please refresh the page and try again.");
        model.put("user", user);
        return renderPage("staff/punishments/view-punishment", model, request, response);
    }
}
