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
public class ShopAction extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    @Filterable("NPC ID")
    private final int npcId;

    @Filterable("Shop ID")
    @ListValue(value = "Shop ID", order = 3)
    private final int shopId;

    private final int itemId;
    private final int amount;

    private final int price;

    @Filterable("UID")
    @ListValue(value = "UID", order = 5)
    private final String uid;

    private final int type;

    @Filterable("IP")
    @ListValue(value = "IP", order = 7)
    private final String ip;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 8, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "Type", order = 6)
    public String getTypeString() {
        return type == 0 ? "Purchased" : "Sold";
    }

    @Filterable("NPC Name")
    @ListValue(value = "NPC Name", order = 2)
    public String getNPCName() {
        return FormatUtils.toNPCName(npcId);
    }

    @Filterable("Item Name")
    @ListValue(value = "Item Name", order = 4)
    public String getItem() {
        return FormatUtils.toItemName(itemId)+" ("+itemId+") x "+FormatUtils.formatRunescapeNumber(amount)+" ("+price+")";
    }

    public Account getUser() {
        return AccountUtils.getAccount(username);
    }

}
