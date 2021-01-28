package com.cryo.entities.accounts.support;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListRowValue;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.modules.account.AccountUtils;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Appeal extends MySQLDao {

    @MySQLDefault
    @SortAndFilter("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @ListValue(value = "Appealee", order = 1, formatAsUser = true, requiresModule = "staff")
    private final String username;

    private final int punishmentId;

    @Filterable("Title")
    @ListValue(value = "Title", order = 2)
    private final String title;

    private final String additional;

    @SortAndFilter(value = "Archived On", onArchive = true)
    @ListValue(value = "Archived On", order = 5, onArchive = true, formatAsTimestamp = true)
    private final Timestamp archived;

    @Filterable(value = "Archiver", onArchive = true)
    @ListValue(value = "Archiver", order = 6, onArchive = true, formatAsUser = true)
    private final String archiver;

    private final int answer;

    private final String reason;

    private final String ip;

    @SortAndFilter("Added On")
    @MySQLDefault
    @ListValue(value = "Added On", order = 3, formatAsTimestamp = true)
    private final Timestamp added;

    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "View Appeal", className="view-appeal", order = 8, isButton = true)
    private Object viewAppeal = "View";

    @Filterable(value = "Answer", onArchive = true)
    @ListValue(value = "Answer", order = 4, onArchive = true)
    public String getAnswer() {
        return answer == 0 ? "Rejected" : "Accepted";
    }

    @ListValue(value = "View Punishment", className="view-appeal-punishment", order = 7, isButton = true)
    public Object viewPunishment = "View";

    public boolean isArchived() {
        return archived != null;
    }

    public Account getArchiverUser() {
        return AccountUtils.getAccount(archiver);
    }

}
