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

    private final String username;

    @Filterable("Title")
    @ListValue(value = "Title", order=1)
    private final String title;

    @Filterable("Offender")
    @ListValue(value = "Offender", order=2, formatAsUser = true)
    private final String offender;

    @Filterable("Rule")
    @ListValue(value = "Rule", order=3)
    private final String rule;

    private final String info;
    private final String proof;

    @MySQLDefault
    @Filterable("Last Action")
    @ListValue(value = "Last Action", order=4)
    private final String lastAction;

    @SortAndFilter("Date of Offence")
    @ListValue(value = "Date of Offence", order=5, formatAsTimestamp = true)
    private final Timestamp date;

    @SortAndFilter(value = "Archived On", onArchive = true)
    @ListValue(value = "Archived On", order=6, formatAsTimestamp = true, onArchive = true)
    private final Timestamp archived;

    @Filterable(value = "Archived By", onArchive = true)
    @ListValue(value = "Archived By", order=7, formatAsUser = true, onArchive = true)
    private final String archiver;

    @MySQLDefault
    @SortAndFilter("Reported On")
    @ListValue(value = "Reported On", order=8, formatAsTimestamp = true)
    private final Timestamp added;

    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "View", className="view-offence", order=9, isButton = true)
    private Object viewButton = "View";
}
