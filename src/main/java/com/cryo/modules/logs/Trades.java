package com.cryo.modules.logs;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.logs.Command;
import com.cryo.entities.logs.Trade;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
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
public class Trades {

    @Endpoint(method = "POST", endpoint = "/logs/trade/load")
    public static String renderCommandLogs(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return com.cryo.modules.account.Login.renderLoginPage("/logs/trade", request, response);
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("refreshable", true);
        model.put("sortable", true);
        model.put("filterable", true);
        model.put("title", "Trade Logs");
        model.put("module", "/logs/trade");
        model.put("moduleId", "trade");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all trades made in-game by players on Cryogen.");
            add("You can filter this list using the buttons on the right.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/logs/trade/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("moduleId", "trade");
        ArrayList<ArrayList<Object>> sortValues = new ArrayList<>();
        if(request.queryParams().contains("sortValues"))
            sortValues = Website.getGson().fromJson(request.queryParams("sortValues"), ArrayList.class);
        ArrayList<ArrayList<Object>> filterValues = new ArrayList<>();
        if(request.queryParams().contains("filterValues"))
            filterValues = Website.getGson().fromJson(request.queryParams("filterValues"), ArrayList.class);
        if(!request.queryParams().contains("page") || !NumberUtils.isDigits(request.queryParams("page")))
            return error("Unable to parse page number. Please refresh the page and try again.");
        int page = Integer.parseInt(request.queryParams("page"));

        ArrayList<Object> values = new ArrayList<>();
        String query = "";

        Object[] condition = ListManager.getCondition(filterValues, Trade.class, false);
        if(condition.length == 2 && condition[0] != null) {
            query += (String) condition[0];
            values.addAll((ArrayList<Object>) condition[1]);
        }
        int total = getConnection("cryogen_logs").selectCount("trade", query, values.toArray());
        String order = ListManager.getOrder(model, sortValues, Trade.class, page, total, false);
        List<Trade> trades = getConnection("cryogen_logs").selectList("trade", query, order, Trade.class, values.toArray());
        if(trades == null)
            return error("Error loading trades. Please try again.");
        ListManager.buildTable(model, "logs", trades, Trade.class, account, sortValues, filterValues, false);
        return renderList(model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/logs/trade/view")
    public static String viewTrade(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse id. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        Trade trade = getConnection("cryogen_logs").selectClass("trade", "id=?", Trade.class, id);
        if(trade == null)
            return error("Unable to find trade. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("trade", trade);
        model.put("traderItems", trade.getTraderItems(1));
        model.put("tradeeItems", trade.getTradeeItems(1));
        model.put("traderPrev", false);
        model.put("traderNext", trade.getTraderItemsPageSize() > 1);
        model.put("tradeePrev", false);
        model.put("tradeeNext", trade.getTradeeItemsPageSize() > 1);
        return renderPage("logs/views/trade", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/logs/trade/view-page")
    public static String viewTradePage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        if(!request.queryParams().contains("id") || !NumberUtils.isDigits(request.queryParams("id")))
            return error("Unable to parse id. Please refresh the page and try again.");
        if(!request.queryParams().contains("traderPage") || !NumberUtils.isDigits(request.queryParams("traderPage")))
            return error("Unable to parse traderPage. Please refresh the page and try again.");
        if(!request.queryParams().contains("tradeePage") || !NumberUtils.isDigits(request.queryParams("tradeePage")))
            return error("Unable to parse tradeePage. Please refresh the page and try again.");
        int id = Integer.parseInt(request.queryParams("id"));
        int traderPage = Integer.parseInt(request.queryParams("traderPage"));
        int tradeePage = Integer.parseInt(request.queryParams("tradeePage"));
        Trade trade = getConnection("cryogen_logs").selectClass("trade", "id=?", Trade.class, id);
        if(trade == null)
            return error("Unable to find trade. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        model.put("trade", trade);
        model.put("traderItems", trade.getTraderItems(traderPage));
        model.put("tradeeItems", trade.getTradeeItems(tradeePage));
        model.put("traderPrev", traderPage > 1);
        model.put("traderNext", trade.getTraderItemsPageSize() > traderPage);
        model.put("tradeePrev", tradeePage > 1);
        model.put("tradeeNext", trade.getTradeeItemsPageSize() > tradeePage);
        return renderPage("logs/views/trade-items", model, request, response);
    }
}
