package com.cryo.entities.logs;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.Session;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import com.cryo.modules.account.AccountUtils;
import com.cryo.utils.BrowserIcons;
import lombok.Data;
import nl.basjes.parse.useragent.UserAgent;

import java.sql.Timestamp;

import static com.cryo.Website.getConnection;

@Data
public class Login extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("User")
    @ListValue(value = "User", order = 1, formatAsUser = true)
    private final String username;

    @Filterable("IP")
    @ListValue(value = "IP", order = 2)
    private final String ip;

    private final int type;

    @Filterable("UUID")
    @ListValue(value = "UUID", order = 4)
    private final String uuid;
    @MySQLRead("user_agent")
    private final String userAgentString;
    private final String sessionId;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 5, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "View", order = 6, isButton = true, className = "view-login")
    private Object view = "View";

    private UserAgent userAgent;

    public UserAgent getUserAgent() {
        if(userAgent == null)
            userAgent = Website.getUserAgentAnalyzer().parse(userAgentString);
        return userAgent;
    }

    public String getIconPath() {
        return BrowserIcons.getPath(getUserAgent().get("AgentName").getValue());
    }

    @SortAndFilter("Type")
    @ListValue(value = "Type", order = 3)
    public String getTypeString() {
        return type == 0 ? "Server" : "Website";
    }

    public boolean isSessionActive() {
        if(sessionId == null) return false;
        Session session = getConnection("cryogen_accounts").selectClass("sessions", "session_id=?", Session.class, sessionId);
        if(session == null) return false;
        return session.getExpiry().getTime() > System.currentTimeMillis();
    }

    public Account getUser() {
        return AccountUtils.getAccount(username);
    }
}
