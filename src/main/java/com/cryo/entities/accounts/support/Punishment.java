package com.cryo.entities.accounts.support;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListRowValue;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.Sortable;
import com.cryo.modules.account.AccountUtils;
import lombok.Data;

import java.sql.Timestamp;

import static com.cryo.Website.getConnection;

@Data
public class Punishment extends MySQLDao {

    @Sortable("ID")
    @MySQLDefault
    @ListValue(value = "ID", order = 0)
    private final int id;

    @MySQLDefault
    private final int appealId;

    @MySQLDefault
    private final int reportId;

    @Filterable(value="User", requiresModule = "staff")
    @ListValue(value = "User", order = 2, formatAsUser = true, requiresModule = "staff")
    private final String username;

    private final int type;

    //TODO - filter booleans with switch
    private final boolean appealable;

    private final Timestamp expiry;

    @Filterable("Punisher")
    @ListValue(value = "Punisher", formatAsUser = true, order = 3)
    private final String punisher;

    @ListValue(value = "Reason", order = 6)
    private final String reason;

    private final String info;

    @Sortable("Archived On")
    @ListValue(value = "Archived On", formatAsTimestamp = true, onArchive = true, order = 8)
    private final Timestamp archived;

    private final String archiver;

    @Sortable("Added On")
    @MySQLDefault
    @ListValue(value = "Added On", formatAsTimestamp = true, order = 7)
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "View", className="view-punishment", order = 12, isButton = true)
    private Object viewPunishment = "View";

    private Appeal appeal;

    @ListValue(value = "Status", order = 4)
    public String getStatus() {
        if(!isArchived()) return "Active";
        if(archiver != null) return "Reversed";
        if(appealId > 0 && getAppeal().isArchived()) return "Appeal "+(getAppeal().getAnswer() == 0 ? "denied" : "accepted");
        if(isExpired()) return "Expired";
        return "N/A";
    }

    //TODO - type filter
    @ListValue(value = "Type", order = 1, returnsValue = true)
    public ListRowValue getTypeRow() {
        ListRowValue value = new ListRowValue(type == 0 ? "Mute" : "Ban");
        value.setOrder(1);
        return value;
    }

    @ListValue(value = "Report", order = 10, returnsValue = true, requiresModule = "staff")
    public ListRowValue getReport(Account account, String module) {
        ListRowValue value;
        if(reportId == 0)
            value = new ListRowValue("No Report");
        else {
            value = new ListRowValue("View");
            value.setClassName("view-punishment-report");
            value.setButton(true);
        }
        value.setOrder(9);
        return value;
    }

    @ListValue(value = "Appeal", order = 11, returnsValue = true)
    public ListRowValue getAppealText(Account account, String module, boolean archived) {
        ListRowValue value;
        if(!appealable)
            value = new ListRowValue("Unappealable");
        else if(appealId == 0 && (module.equals("staff") || archived)) {
            value = new ListRowValue("No Appeal");
        } else {
            value = new ListRowValue(appealId == 0 ? "Appeal" : "View");
            value.setClassName(appealId == 0 ? "appeal" : "view-punishment-appeal");
            value.setButton(true);
        }
        value.setOrder(10);
        return value;
    }

    //TODO - figure out how to filter/sort this
    //show only actual date in archive. Use 'status' to show if it was archived because of expiry
    @ListValue(value = "Expires", order = 5, returnsValue = true)
    public ListRowValue getExpiry() {
        ListRowValue value = new ListRowValue(expiry == null ? "Does not Expire" : expiry);
        if(isExpired())
            value.setClassName("color-green");
        if(expiry != null)
            value.setShouldFormatAsTimestamp(true);
        value.setOrder(4);
        return value;
    }

    @Filterable(value = "Archived By", requiresModule = "staff", onArchive = true)
    @ListValue(value = "Archived By", order = 9, returnsValue = true, onArchive = true)
    public ListRowValue getArchivedBy() {
        ListRowValue value;
        if(archived != null) {
            value = new ListRowValue(archiver);
            value.setShouldFormatAsUser(true);
        } else
            value = new ListRowValue("N/A");
        value.setOrder(8);
        return value;
    }

    public Timestamp getExpiryStamp() {
        return expiry;
    }

    public boolean isExpired() {
        return expiry != null && expiry.getTime() < System.currentTimeMillis();
    }

    public boolean isArchived() {
        return archived != null || isExpired() || (appealId > 0 && getAppeal().isArchived());
    }

    public Appeal getAppeal() {
        if(appeal == null)
            appeal = getConnection("cryogen_punish").selectClass("appeals", "id=?", Appeal.class, appealId);
        return appeal;
    }

    public Account getArchiverAccount() {
        return AccountUtils.getAccount(archiver);
    }

    public Account getAccount() {
        return AccountUtils.getAccount(username);
    }

}
