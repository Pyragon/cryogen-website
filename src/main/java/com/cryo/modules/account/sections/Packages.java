package com.cryo.modules.account.sections;

import com.cryo.Website;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import com.cryo.entities.shop.Package;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class Packages {

    @Endpoint(method = "POST", endpoint = "/account/packages/load")
    public static String renderPackagesPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/account/packages", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("archivable", true);
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Packages");
        model.put("module", "/account/packages");
        model.put("moduleId", "account-packages");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists the packages you have purchased through the Cryogen shop.");
            add("You may redeem any package to your own account through this page.");
            add("More information on how to trade packages can be found on the forums.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/account/packages/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("moduleId", "account-packages");
        ArrayList<ArrayList<Object>> sortValues = new ArrayList<>();
        if(request.queryParams().contains("sortValues"))
                sortValues = Website.getGson().fromJson(request.queryParams("sortValues"), ArrayList.class);
        ArrayList<ArrayList<Object>> filterValues = new ArrayList<>();
        if(request.queryParams().contains("filterValues"))
            filterValues = Website.getGson().fromJson(request.queryParams("filterValues"), ArrayList.class);
        boolean archived = Boolean.parseBoolean(request.queryParamOrDefault("archived", "false"));
        String order = ListManager.getOrder(sortValues, Package.class, archived);

        ArrayList<Object> values = new ArrayList<>();
        String query = "username=? AND active=?";
        values.add(account.getUsername());
        values.add(archived ? 0 : 1);

        Object[] condition = ListManager.getCondition(filterValues, Package.class, archived);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        List<Package> packages = getConnection("cryogen_shop").selectList("packages", query, order, Package.class, values.toArray());
        if(packages == null)
            return error("Error loading packages. Please try again.");
        ListManager.buildTable(model, "account", packages, Package.class, account, sortValues, filterValues, archived);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/account/packages/redeem")
    public static String redeemPackage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id"))) return error("Error parsing id.");
        int id = Integer.parseInt(request.queryParams("id"));
        Package pack = getConnection("cryogen_shop").selectClass("packages", "id=?", Package.class, id);
        if(pack == null || !pack.isActive() || !pack.getUsername().equals(account.getUsername())) return error("Error loading package. Please refresh the page and try again.");
        //REDEEM PACKAGE TODO
        return error("TODO - finish");
    }
}
