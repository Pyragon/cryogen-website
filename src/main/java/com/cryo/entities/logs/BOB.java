package com.cryo.entities.logs;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import com.cryo.utils.FormatUtils;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BOB extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    private final int npcId;

    private final int type;

    private final int itemId;
    private final int amount;

    @Filterable("UID")
    @ListValue(value = "UID", order = 5)
    private final String uid;

    @Filterable("IP")
    @ListValue(value = "IP", order = 6)
    private final String ip;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 7, formatAsTimestamp = true)
    private final Timestamp added;

    @Filterable("NPC")
    @ListValue(value = "NPC", order = 2)
    public String getNPCName() {
        return FormatUtils.toNPCName(npcId);
    }

    @Filterable("Type")
    @ListValue(value = "Type", order = 3)
    public String getType() {
        return type == 0 ? "Deposited" : "Withdrawn";
    }

    @Filterable("Item Name")
    @ListValue(value = "Item Name", order = 4)
    public String getItem() {
        return FormatUtils.toItemName(itemId)+" ("+itemId+") x "+FormatUtils.formatRunescapeNumber(amount);
    }
}
