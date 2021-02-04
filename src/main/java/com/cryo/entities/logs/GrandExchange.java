package com.cryo.entities.logs;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListRowValue;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.Sortable;
import com.cryo.utils.FormatUtils;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class GrandExchange extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    private final String buyer;

    private final int type;

    private final int itemId;
    private final int amount;
    private final int price;

    @Filterable("UID")
    @ListValue(value = "UID", order = 5)
    private final String uid;

    @Filterable("IP")
    @ListValue(value = "IP", order = 6)
    private final String ip;

    @MySQLDefault
    @Filterable("Added")
    @ListValue(value = "Added", order = 7, formatAsTimestamp = true)
    private final Timestamp added;

    @Filterable("Item Name")
    @ListValue(value = "Item Name", order = 4)
    public String getItem() {
        return FormatUtils.toItemName(itemId)+" ("+itemId+") x "+FormatUtils.formatRunescapeNumber(amount) +" ("+price+")";
    }

    @Filterable("Buyer")
    @ListValue(value = "Buyer", order = 3, returnsValue  = true)
    public ListRowValue getBuyerString() {
        ListRowValue value = new ListRowValue(buyer == null ? "N/A" : buyer);
        if(buyer != null)
            value.setShouldFormatAsUser(true);
        value.setOrder(3);
        return value;
    }

    @ListValue(value = "Action", order = 2)
    public String getAction() {
        return type == 0 ? "Added to GE" : type == 1 ? "Sold for "+price : "Removed from GE";
    }

}
