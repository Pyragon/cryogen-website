package com.cryo.entities.logs;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import com.cryo.utils.FormatUtils;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class BOBDeath extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    private final int npcId;

    private final String itemsLost;

    @Filterable("World Tile")
    @ListValue(value = "World Tile", order = 4, className = "bob-world-tile")
    private final String worldTile;

    @Filterable("IP")
    @ListValue(value = "IP", order = 5)
    private final String ip;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 6, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "View", order = 7, isButton = true, className = "view-bob")
    private Object view = "View";

    @Filterable("NPC")
    @ListValue(value = "NPC", order = 2)
    public String getNPCName() {
        return FormatUtils.toNPCName(npcId);
    }

    public ArrayList<LinkedTreeMap<String, Object>> getItemsLost() {
        return Website.getGson().fromJson(itemsLost, ArrayList.class);
    }

    @SortAndFilter("Items Lost")
    @ListValue(value = "Items Lost", order = 3)
    public int getItemsLostSize() {
        return getItemsLost().size();
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
