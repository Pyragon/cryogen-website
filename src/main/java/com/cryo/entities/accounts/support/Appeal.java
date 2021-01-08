package com.cryo.entities.accounts.support;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListRowValue;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
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

    @ListValue(value = "View Punishment", order = 8, className="view-punishment", isButton = true)
    private final int punishId;

    private final int type;

    @Filterable("Title")
    @ListValue(value = "Title", order = 3)
    private final String title;

    private final String message;

    @Filterable("Last Action")
    @ListValue(value = "Last Action", order = 4)
    private final String lastAction;

    @SortAndFilter("Answered On")
    @ListValue(value = "Answered On", order = 5, onArchive = true, formatAsTimestamp = true)
    private final Timestamp answered;

    private final String answerer;

    private final int answer;

    private final String reason;

    @SortAndFilter("Added On")
    @MySQLDefault
    @ListValue(value = "Added On", order = 7, formatAsTimestamp = true)
    private final Timestamp added;

    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "View Appeal", className="view-appeal", order = 9, isButton = true)
    private Object viewAppeal = "View";

    @SortAndFilter("Type")
    @ListValue(value = "Type", order = 2)
    public String getType() {
        return type == 0 ? "Mute" : "Ban";
    }

    @Filterable("Answer")
    @ListValue(value = "Answer", order = 6, onArchive = true)
    public String getAnswer() {
        return answer == 0 ? "Rejected" : "Accepted";
    }

}
