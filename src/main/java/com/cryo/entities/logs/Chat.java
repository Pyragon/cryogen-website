package com.cryo.entities.logs;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.Sortable;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Chat extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID")
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", formatAsUser = true)
    private final String username;

    @Filterable("World Tile")
    @ListValue(value = "World Tile", className = "chat-world-tile")
    private final String worldTile;

    @Filterable("Message")
    @ListValue(value = "Message")
    private final String message;

    @MySQLDefault
    @Filterable("Added")
    @ListValue(value = "Added", formatAsTimestamp = true)
    private final Timestamp added;

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
        return getMapLink();
    }

    public String getMapLink() {
        return "/map?x="+getX()+"&y="+getY()+"&plane="+getPlane();
    }

    public int getRegionId() {

    }
}
