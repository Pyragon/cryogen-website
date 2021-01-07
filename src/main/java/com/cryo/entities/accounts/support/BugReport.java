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
public class BugReport extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    private final String username;

    @Filterable("Title")
    @ListValue(value = "Title", order = 1)
    private final String title;

    @SortAndFilter(value = "Replicatable?")
    @ListValue(value = "Can be Replicated", order = 2)
    private final boolean replicated;

    @SortAndFilter("Date seen")
    @ListValue(value = "Date seen", formatAsTimestamp = true, order = 3)
    private final Timestamp seen;

    private final String info;

    @ListValue(value = "Last Action", order = 4)
    private final String lastAction;

    @Filterable("Archived On")
    @ListValue(value = "Archived On", formatAsTimestamp = true, order = 5)
    private final Timestamp archived;

    @Filterable("Archived By")
    @ListValue(value = "Archived By", formatAsUser = true, order = 6)
    private final String archiver;

    @Filterable("Added")
    @ListValue(value = "Added", formatAsTimestamp = true, order = 7)
    @MySQLDefault
    private final Timestamp added;

    @MySQLDefault
    private final Timestamp updated;
}
