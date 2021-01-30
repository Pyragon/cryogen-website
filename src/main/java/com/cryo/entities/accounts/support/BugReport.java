package com.cryo.entities.accounts.support;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import com.cryo.modules.account.AccountUtils;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BugReport extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable(value = "Reporter", requiresModule = "staff")
    @ListValue(value = "Reporter", requiresModule = "staff", order = 1, formatAsUser = true)
    private final String username;

    @Filterable("Title")
    @ListValue(value = "Title", order = 2)
    private final String title;

    private final int type;

    @SortAndFilter(value = "Is Replicable?")
    @ListValue(value = "Is Replicable?", order = 4)
    private final boolean replicable;

    @SortAndFilter("Date seen")
    @ListValue(value = "Date seen", formatAsTimestamp = true, order = 5)
    private final Timestamp seen;

    private final String additional;

    @Filterable(value = "Archived On", onArchive = true)
    @ListValue(value = "Archived On", formatAsTimestamp = true, order = 6, onArchive = true)
    private final Timestamp archived;

    @Filterable(value = "Archived By", onArchive = true)
    @ListValue(value = "Archived By", formatAsUser = true, order = 7, onArchive = true)
    private final String archiver;

    @Filterable("Added")
    @ListValue(value = "Added", formatAsTimestamp = true, order = 8)
    @MySQLDefault
    private final Timestamp added;

    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "View", order = 9, isButton = true, className = "view-bug-report")
    private Object button = "View";

    @ListValue(value = "Type", order = 3)
    public String getTypeString() {
        return type == 0 ? "Server" : "Website";
    }

    public Account getReporter() {
        return AccountUtils.getAccount(username);
    }
}
