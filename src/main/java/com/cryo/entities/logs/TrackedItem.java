package com.cryo.entities.logs;

import com.cryo.entities.list.ListRowValue;
import com.cryo.entities.list.ListValue;
import com.cryo.utils.FormatUtils;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TrackedItem {

    @ListValue(value = "ID", order = 0)
    private final int id;

    @ListValue(value = "Type", order = 1)
    private final String type;

    @ListValue(value = "Start", order = 2, formatAsUser = true)
    private final String username;

    @ListValue(value = "Value", order = 3)
    private final String value;

    private final Object value2;

    private final String worldTile;

    @ListValue(value = "Added", formatAsTimestampHour = true, order = 6)
    private final Timestamp added;

    @ListValue(value = "End", order = 4, returnsValue = true)
    public ListRowValue getEnd() {
        String name;
        boolean isUser = false;
        if(value2 == null)
            name = "";
        else if(value2 instanceof Integer)
            name = FormatUtils.toNPCName((int) value2);
        else if(value2 instanceof String) {
            name = (String) value2;
            isUser = true;
        } else
            name = "";
        ListRowValue value = new ListRowValue(name);
        if(isUser)
            value.setShouldFormatAsUser(true);
        value.setOrder(4);
        return value;
    }

    @ListValue(value = "World Tile", returnsValue = true, order = 5)
    public ListRowValue getWorldTile() {
        ListRowValue value = new ListRowValue(worldTile == null ? "N/A" : worldTile);
        value.setOrder(5);
        if(worldTile != null)
            value.setClassName("item-world-tile");
        return value;
    }

    public int getX() {
        if(worldTile == null || worldTile.equals("N/A")) return -1;
        return Integer.parseInt(worldTile.split(",")[0]);
    }
    public int getY() {
        if(worldTile == null || worldTile.equals("N/A")) return -1;
        return Integer.parseInt(worldTile.split(",")[1]);
    }
    public int getPlane() {
        if(worldTile == null || worldTile.equals("N/A")) return -1;
        return Integer.parseInt(worldTile.split(",")[2]);
    }

    public String getExtra() {
        return getMapLink();
    }

    public String getMapLink() {
        if(getX() == -1) return "";
        return "/map?x="+getX()+"&y="+getY()+"&plane="+getPlane();
    }
}
