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
public class NPCDrop extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    private final int npcId;

    private final int itemId;
    private final int amount;

    @Filterable("UID")
    @ListValue(value = "UID", order = 4)
    private final String uid;

    @Filterable("World Tile")
    @ListValue(value = "World Tile", order = 5)
    private final String worldTile;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 6)
    private final Timestamp added;

    @ListValue(value = "NPC Name", order = 2)
    public String getNPCName() {
        return FormatUtils.toNPCName(npcId);
    }

    @Filterable("Item Name")
    @ListValue(value = "Item Name", order = 3)
    public String getItem() {
        return FormatUtils.toItemName(itemId)+" ("+itemId+") x "+FormatUtils.formatRunescapeNumber(amount);
    }
}
