package com.cryo.entities.logs;

import com.cryo.Website;
import com.cryo.cache.loaders.NPCDefinitions;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import com.cryo.modules.account.AccountUtils;
import com.cryo.utils.FormatUtils;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Death extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    @Filterable("WorldTile")
    @ListValue(value = "WorldTile", order = 2, className = "death-world-tile")
    private final String worldTile;

    @Filterable("NPC Id")
    private final int killedBy;

    private final String itemsLost;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 6, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "View", order = 7, isButton = true, className = "view-death")
    private Object view = "View";

//    @Filterable("Location")
//    @ListValue(value = "Location", order = 3)
//    public String getLocationName() {
//        return "";
//    }

    @Filterable("NPC Name")
    @ListValue(value = "Killed By", order = 4)
    public String getNPCName() {
        return FormatUtils.toNPCName(killedBy);
    }

    public int getLevel() {
        NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(killedBy);
        if(defs == null) return 0;
        return defs.combatLevel;
    }

    public ArrayList<LinkedTreeMap<String, Integer>> getItemsLost() {
        return Website.getGson().fromJson(itemsLost, ArrayList.class);
    }

    @Sortable("Items Lost")
    @ListValue(value = "Items Lost", order = 5)
    public int getItemsLostSize() {
        return getItemsLost().size();
    }

    public Account getUser() {
        return AccountUtils.getAccount(username);
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
