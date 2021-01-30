package com.cryo.entities.accounts.support;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.*;
import com.cryo.modules.account.AccountUtils;
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

    private final int punishmentId;

    @Filterable("Title")
    @ListValue(value = "Title", order=2)
    private final String title;

    @Filterable("Offender")
    @ListValue(value = "Offender", order=3, formatAsUser = true)
    private final String offender;

    private final boolean verified;

    @Filterable("Rule")
    @ListValue(value = "Rule", order=4)
    private final String rule;

    private final String additional;
    private final String proof;

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

    @ListValue(value = "View", className="view-player-report", order=11, isButton = true)
    private Object viewButton = "View";

    @ListValue(value = "Punishment", order=10, returnsValue = true, onArchive = true)
    public ListRowValue getPunishment() {
        ListRowValue value = new ListRowValue(punishmentId == 0 ? "No Punishment" : "View");
        if(punishmentId != 0) {
            value.setButton(true);
            value.setClassName("view-report-punishment");
        }
        value.setOrder(10);
        return value;
    }

    public Account getArchiver() {
        return AccountUtils.getAccount(archiver);
    }

    public Account getReporter() {
        return AccountUtils.getAccount(username);
    }

    public Account getOffender() {
        return AccountUtils.getAccount(offender);
    }

    public String getOffenderName() {
        return offender;
    }
}
