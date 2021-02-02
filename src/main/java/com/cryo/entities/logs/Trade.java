package com.cryo.entities.logs;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import com.cryo.modules.account.AccountUtils;
import com.cryo.utils.Utilities;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

@Data
public class Trade extends MySQLDao {

    @MySQLDefault
    @Sortable(value = "ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("Trader")
    @ListValue(value = "Trader", order = 1, formatAsUser = true)
    private final String trader;

    @Filterable("Tradee")
    @ListValue(value = "Tradee", order = 2, formatAsUser = true)
    private final String tradee;

    private final String traderItems;
    private final String tradeeItems;

    @Filterable("Trader IP")
    @ListValue(value = "Trader IP", order = 4)
    private final String traderIp;

    @Filterable("Tradee IP")
    @ListValue(value = "Tradee IP", order = 5)
    private final String tradeeIp;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 6, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "View", order = 7, isButton = true, className = "view-trade")
    private Object view = "View";

    @ListValue(value = "Traded", order = 3)
    public String getTradedInfo() {
        return getTraderItems().size()+" items <-> "+getTradeeItems().size()+" items";
    }

    public ArrayList<LinkedTreeMap<String, Object>> getTraderItems(int page) {
        ArrayList<LinkedTreeMap<String, Object>> items = Website.getGson().fromJson(this.traderItems, ArrayList.class);
        ArrayList<LinkedTreeMap<String, Object>> results = new ArrayList<>();
        int start = (page-1)*10;
        for(int i = start; i < start+10; i++) {
            if(i >= items.size()) continue;
            results.add(items.get(i));
        }
        return results;
    }

    public ArrayList<LinkedTreeMap<String, Object>> getTradeeItems(int page) {
        ArrayList<LinkedTreeMap<String, Object>> items = Website.getGson().fromJson(this.tradeeItems, ArrayList.class);
        ArrayList<LinkedTreeMap<String, Object>> results = new ArrayList<>();
        int start = (page-1)*10;
        for(int i = start; i < start+10; i++) {
            if(i >= items.size()) continue;
            results.add(items.get(i));
        }
        return results;
    }

    public ArrayList<LinkedTreeMap<String, Object>> getTraderItems() {
        return Website.getGson().fromJson(traderItems, ArrayList.class);
    }

    public ArrayList<LinkedTreeMap<String, Object>> getTradeeItems() {
        return Website.getGson().fromJson(tradeeItems, ArrayList.class);
    }

    public Account getTrader() {
        return AccountUtils.getAccount(trader);
    }

    public Account getTradee() {
        return AccountUtils.getAccount(tradee);
    }

    public int getTraderItemsPageSize() {
        return (int) Utilities.roundUp(getTradeeItems().size(), 10);
    }

    public int getTradeeItemsPageSize() {
        return (int) Utilities.roundUp(getTradeeItems().size(), 10);
    }
}
