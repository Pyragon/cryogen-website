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
import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class PVP extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    @Filterable("Killer")
    @ListValue(value = "Killer", order = 2, formatAsUser = true)
    private final String killer;

    @Filterable("World Tile")
    @ListValue(value = "World Tile", order = 3, className = "pvp-world-tile")
    private final String worldTile;

    private final String itemsLost;

    @Filterable("IP")
    @ListValue(value = "IP", order = 5)
    private final String ip;

    @Filterable("Killer IP")
    @ListValue(value = "Killer IP", order = 6)
    private final String killerIp;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 7, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "View", order = 8, isButton = true, className = "view-pvp")
    private Object view = "View";

    public ArrayList<LinkedTreeMap<String, Object>> getItemsLost() {
        return Website.getGson().fromJson(itemsLost, ArrayList.class);
    }

    @Sortable("Items Lost")
    @ListValue(value = "Items Lost", order = 4)
    public int getItemsLostSize() {
        return getItemsLost().size();
    }

    public Account getUser() {
        return AccountUtils.getAccount(username);
    }

    public Account getKiller() {
        return AccountUtils.getAccount(killer);
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
