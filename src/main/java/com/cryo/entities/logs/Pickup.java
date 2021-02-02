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
public class Pickup extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    private final int itemId;
    private final int amount;

    @Filterable("UID")
    @ListValue(value = "UID", order = 4)
    private final String uid;

    @Filterable("WorldTile")
    @ListValue(value = "WorldTile", order = 2, className = "pickup-world-tile")
    private final String worldTile;

    @Filterable("IP")
    @ListValue(value = "IP", order = 5)
    private final String ip;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 6, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "Item", order = 3)
    public String getItem() {
        return FormatUtils.toItemName(itemId)+" ("+itemId+") x "+FormatUtils.formatRunescapeNumber(amount);
    }

    public int getX() {
        return Integer.parseInt(worldTile.split(",")[0]);
    }
    public int getY() {
        return Integer.parseInt(worldTile.split(",")[1]);
    }
    public int getPlane() {
        return Integer.parseInt(worldTile.split(",")[2]);
    }

    public String getExtra() {
        return "/map?x="+getX()+"&y="+getY()+"&plane="+getPlane();
    }

    public String getMapLink() {
        return "/map?x="+getX()+"&y="+getY()+"&plane="+getPlane();
    }

}
