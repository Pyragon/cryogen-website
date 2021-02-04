package com.cryo.entities.logs;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.Sortable;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Dice extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("Host")
    @ListValue(value = "Host", order = 1, formatAsUser = true)
    private final String host;

    @Filterable("Dicer")
    @ListValue(value = "Dicer", order = 2, formatAsUser = true)
    private final String dicer;

    @Filterable("Winner")
    @ListValue(value = "Winner", order = 3, formatAsUser = true)
    private final String winner;

    private final String hostStake;
    private final String dicerStake;

    @Filterable("Host IP")
    @ListValue(value = "Host IP", order = 5)
    private final String hostIp;

    @Filterable("Dicer IP")
    @ListValue(value = "Dicer IP", order = 6)
    private final String dicerIp;

    @Filterable("Added")
    @ListValue(value = "Added", order = 7, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "View", order = 8, isButton = true, className = "view-dice")
    private Object view = "View";

    public ArrayList<LinkedTreeMap<String, Object>> getHostStake() {
        return Website.getGson().fromJson(hostStake, ArrayList.class);
    }

    public ArrayList<LinkedTreeMap<String, Object>> getDicerStake() {
        return Website.getGson().fromJson(dicerStake, ArrayList.class);
    }

    @ListValue(value = "Stake", order = 4)
    public String getStake() {
        return getHostStake().size()+" <-> "+getDicerStake().size();
    }
}
