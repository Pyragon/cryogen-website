package com.cryo.entities.logs;

import com.cryo.cache.loaders.ItemDefinitions;
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
public class Drop extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    @Filterable("Item ID")
    private final int itemId;
    @Filterable("Amount")
    private final int amount;

    @Filterable("UID")
    @ListValue(value = "UID", order = 3)
    private final String uid;

    private final int dropType;

    @Filterable("WorldTile")
    @ListValue(value = "WorldTile", order = 2, className = "drop-world-tile")
    private final String worldTile;

    @Filterable("IP")
    @ListValue(value = "IP", order = 6)
    private final String ip;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 7, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "Item", order = 3)
    public String getItem() {
        return FormatUtils.toItemName(itemId)+" ("+itemId+") x "+FormatUtils.formatRunescapeNumber(amount);
    }

    public Account getUser() {
        return AccountUtils.getAccount(username);
    }

    public int getX() {
        if(worldTile.equals("N/A")) return -1;
        return Integer.parseInt(worldTile.split(",")[0]);
    }
    public int getY() {
        if(worldTile.equals("N/A")) return -1;
        return Integer.parseInt(worldTile.split(",")[1]);
    }
    public int getPlane() {
        if(worldTile.equals("N/A")) return -1;
        return Integer.parseInt(worldTile.split(",")[2]);
    }

    public String getExtra() {
        if(getX() == -1) return "";
        return "/map?x="+getX()+"&y="+getY()+"&plane="+getPlane();
    }

    public String getMapLink() {
        return "/map?x="+getX()+"&y="+getY()+"&plane="+getPlane();
    }

    @Filterable("Drop Type")
    @ListValue(value = "Drop Type", order = 4)
    public String getDropType() {
        return dropType == 0 ? "Dropped" : "Destroyed";
    }

}
