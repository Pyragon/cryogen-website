package com.cryo.modules.staff;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.support.Appeal;
import com.cryo.entities.accounts.support.Recovery;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoint;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.cryo.utils.BCrypt;
import com.cryo.utils.Utilities;
import com.mysql.cj.util.StringUtils;
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

    @Endpoint(method = "POST", endpoint = "/staff/recoveries/load")
    public static String renderAppealsPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/staff/recoveries", request, response);
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("sortable", true);
        model.put("refreshable", true);
        model.put("filterable", true);
        model.put("title", "Recoveries");
        model.put("module", "/staff/recoveries");
        model.put("moduleId", "recoveries");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all recoveries made by players on Cryogen.");
            add("You can filter this list using the buttons on the right.");
            add("Please completely read the forums before making any decisions on recoveries.");
            add("As an admin, you are given the responsibility to accept/deny recoveries based on the information provided.");
            add("If you are at all unsure about a recovery. Please consult Cody or fellow admins.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/staff/recoveries/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("moduleId", "recoveries");
        ArrayList<ArrayList<Object>> sortValues = new ArrayList<>();
        if(request.queryParams().contains("sortValues"))
            sortValues = Website.getGson().fromJson(request.queryParams("sortValues"), ArrayList.class);
        ArrayList<ArrayList<Object>> filterValues = new ArrayList<>();
        if(request.queryParams().contains("filterValues"))
            filterValues = Website.getGson().fromJson(request.queryParams("filterValues"), ArrayList.class);
        boolean archived = Boolean.parseBoolean(request.queryParamOrDefault("archived", "false"));
        String order = ListManager.getOrder(sortValues, Recovery.class, archived);

        ArrayList<Object> values = new ArrayList<>();
        String query = "status"+(archived ? "!=" : "=")+"1";

        Object[] condition = ListManager.getCondition(filterValues, Recovery.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        List<Recovery> recoveries = getConnection("cryogen_recovery").selectList("recoveries", query, order, Recovery.class, values.toArray());
        if(recoveries == null)
            return error("Error loading recoveries. Please try again.");
        model.put("archived", archived);
        ListManager.buildTable(model, "staff", recoveries, Recovery.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/staff/recoveries/view")
    public static String viewRecoveryViaStaff(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse ID.");
        int id = Integer.parseInt(request.queryParams("id"));
        Recovery recovery = getConnection("cryogen_recovery").selectClass("recoveries", "id=?", Recovery.class, id);
        if(recovery == null)
            return error("Unable to find recovery. Please refresh the page and try again.");
        Account user = AccountUtils.getAccount(recovery.getUsername());
        if(user == null)
            return error("Unable to find creator of recovery. Please refresh the page and try again.");
        model.put("recovery", recovery);
        model.put("user", user);
        return renderPage("account/support/recovery/view-recovery", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/staff/recoveries/respond/noty")
    public static String viewRecoveryRespond(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse ID.");
        int id = Integer.parseInt(request.queryParams("id"));
        Recovery recovery = getConnection("cryogen_recovery").selectClass("recoveries", "id=?", Recovery.class, id);
        if(recovery == null)
            return error("Unable to find recovery. Please refresh the page and try again.");
        if(recovery.isArchived())
            return error("This recovery has already been archived. Please refresh the page and try again.");
        return renderPage("account/support/recovery/respond-recovery", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/staff/recoveries/respond")
    public static String submitRecoveryResponse(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse ID.");
        int id = Integer.parseInt(request.queryParams("id"));
        Recovery recovery = getConnection("cryogen_recovery").selectClass("recoveries", "id=?", Recovery.class, id);
        if(recovery == null)
            return error("Unable to find recovery. Please refresh the page and try again.");
        if(recovery.isArchived())
            return error("This recovery has already been archived. Please refresh the page and try again.");
        String reason = request.queryParams("reason");
        if(StringUtils.isNullOrEmpty(reason) || reason.length() < 6 || reason.length() > 200)
            return error("Your reason must be between 6 and 200 characters.");
        String type = request.queryParams("type");
        if(StringUtils.isNullOrEmpty(type) || !(type.equals("decline") || type.equals("accept")))
            return error("Invalid type. Please refresh the page and try again.");
        int nStatus = type.equals("accept") ? 4 : 2;
        String set = "status=?, decider=?, reason=?, decided=NOW()";
        ArrayList<Object> values = new ArrayList<Object>() {{
            add(nStatus);
            add(account.getUsername());
            add(reason);
        }};
        String newPass;
        if(nStatus == 4) {
            newPass = Utilities.generateRandomString(8);
            set += ", new_password=?";
            values.add(newPass);
            Account user = AccountUtils.getAccount(recovery.getUsername());
            if(user == null)
                return error("Unable to find recovery account. Please refresh the page and try again.");
            String salt = user.getSalt();
            newPass = BCrypt.hashPassword(newPass, salt);
            getConnection("cryogen_global").set("player_data", "password=?,password_reset_required=1", "username=?", newPass, recovery.getUsername());
            getConnection("cryogen_accounts").delete("sessions", "username=?", recovery.getUsername());
        }
        values.add(id);
        getConnection("cryogen_recovery").set("recoveries", set, "id=?", values.toArray());
        return success("");
    }
}
