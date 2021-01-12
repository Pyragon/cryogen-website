package com.cryo.entities.accounts;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.utils.BrowserIcons;
import lombok.Data;
import nl.basjes.parse.useragent.UserAgent;

import java.sql.Timestamp;

@Data
public class Session extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String username;
    private final String sessionId;
    @MySQLRead("user_agent")
    private final String userAgentString;
    private final String visitorId;
    private final Timestamp expiry;
    @MySQLDefault
    private final Timestamp added;

    private UserAgent userAgent;

    private boolean current;

    public UserAgent getUserAgent() {
        if(userAgent == null)
            userAgent = Website.getUserAgentAnalyzer().parse(userAgentString);
        return userAgent;
    }

    public String getIconPath() {
        return BrowserIcons.getPath(getUserAgent().get("AgentName").getValue());
    }

}
