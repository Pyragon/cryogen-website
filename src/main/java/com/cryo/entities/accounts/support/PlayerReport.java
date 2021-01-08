package com.cryo.entities.accounts.support;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PlayerReport extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order=0)
    private final int id;

    @ListValue(value = "Reporter", requiresModule = "staff", order = 1, formatAsUser = true)
    private final String username;

    @Filterable("Title")
    @ListValue(value = "Title", order=2)
    private final String title;

    @Filterable("Offender")
    @ListValue(value = "Offender", order=3, formatAsUser = true)
    private final String offender;

    @Filterable("Rule")
    @ListValue(value = "Rule", order=4)
    private final String rule;

    private final String info;
    private final String proof;

    @MySQLDefault
    @Filterable("Last Action")
    @ListValue(value = "Last Action", order=5)
    private final String lastAction;

    @SortAndFilter("Date of Offence")
    @ListValue(value = "Date of Offence", order=6, formatAsTimestamp = true)
    private final Timestamp date;

    @SortAndFilter(value = "Archived On", onArchive = true)
    @ListValue(value = "Archived On", order=7, formatAsTimestamp = true, onArchive = true)
    private final Timestamp archived;

    @Filterable(value = "Archived By", onArchive = true)
    @ListValue(value = "Archived By", order=8, formatAsUser = true, onArchive = true)
    private final String archiver;

    @MySQLDefault
    @SortAndFilter("Reported On")
    @ListValue(value = "Reported On", order=9, formatAsTimestamp = true)
    private final Timestamp added;

    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "View", className="view-offence", order=10, isButton = true)
    private Object viewButton = "View";
}
