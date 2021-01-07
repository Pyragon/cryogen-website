package com.cryo.entities.accounts.support;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.list.ListRowValue;
import com.cryo.entities.list.ListValue;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Punishment extends MySQLDao {

    @MySQLDefault
    @ListValue(value = "ID", order = 0)
    private final int id;

    private final int appealId;

    private final String username;

    private final int type;

    private final Timestamp expiry;

    @ListValue(value = "Punisher", formatAsUser = true, order = 2)
    private final String punisher;

    @ListValue(value = "Reason", order = 3)
    private final String reason;

    private final String info;

    private final String proof;

    @ListValue(value = "Archived On", formatAsTimestamp = true, onArchive = true, order = 6)
    private final Timestamp archived;

    private final String archiver;

    @MySQLDefault
    @ListValue(value = "Added On", formatAsTimestamp = true, order = 5)
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "View", className="view-punishment", order = 8, isButton = true)
    private Object viewPunishment = "View";

    @ListValue(value = "Type", order = 1, returnsValue = true)
    public ListRowValue getType() {
        ListRowValue value = new ListRowValue(type == 0 ? "Mute" : "Ban");
        value.setOrder(1);
        return value;
    }

    @ListValue(value = "Appeal", order = 7, returnsValue = true, notOnArchive = true)
    public ListRowValue getAppealText() {
        ListRowValue value = new ListRowValue("Appeal");
        value.setClassName("appeal");
        value.setButton(true);
        value.setOrder(7);
        return value;
    }

    @ListValue(value = "Expires", order = 4, returnsValue = true)
    public ListRowValue getExpiry() {
        ListRowValue value = new ListRowValue("Does not Expire");
        value.setOrder(4);
        return value;
    }
}
