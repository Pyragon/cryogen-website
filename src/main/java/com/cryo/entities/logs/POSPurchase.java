package com.cryo.entities.logs;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import com.cryo.modules.account.AccountUtils;
import com.cryo.utils.FormatUtils;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class POSPurchase extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    @Filterable("Owner")
    @ListValue(value = "Owner", order = 2, formatAsUser = true)
    private final String owner;

    private final int itemId;
    private final int amount;

    @SortAndFilter("Price")
    @ListValue(value = "Price", order = 4)
    private final int price;

    @Filterable("UID")
    @ListValue(value = "UID", order = 5)
    private final String uid;

    @Filterable("IP")
    @ListValue(value = "IP", order = 6)
    private final String ip;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 7)
    private final Timestamp added;

    @Filterable("Item Name")
    @ListValue(value = "Item Name", order = 3)
    public String getItem() {
        return FormatUtils.toItemName(itemId)+" ("+itemId+") x "+FormatUtils.formatRunescapeNumber(amount)+" ("+price+")";
    }

}
