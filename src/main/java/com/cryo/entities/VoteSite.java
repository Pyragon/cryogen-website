package com.cryo.entities;

import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.ListValue;
import lombok.Data;

import java.sql.Timestamp;

import static com.cryo.Website.getConnection;

@Data
public class VoteSite extends MySQLDao {

    @MySQLDefault
    @ListValue(value = "Site ID", order = 0)
    private final int id;
    @ListValue(value = "Site Name", order = 1)
    private final String name;
    @MySQLRead("url")
    private final String URL;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "Vote", className = "", isButton = true, order = 5)
    private Object voteButton = "Vote";

    @ListValue(value = "Total Votes", formatAsNumber = true, order = 2)
    public int getTotalVotes(Account account) {
        return getConnection("cryogen_vote").selectCount("votes", "username=? AND site_id=?", account.getUsername(), id);
    }

    @ListValue(value = "Last Vote", formatAsTime = true, order = 3)
    public Timestamp getLastVote(Account account) {
        Vote vote = getConnection("cryogen_vote").selectClass("votes", "username=? AND site_id=?", "ORDER BY added DESC LIMIT 1", Vote.class, account.getUsername(), id);
        if(vote == null) return null;
        return vote.getAdded();
    }

    //TODO - figure out how to countdown
    @ListValue(value = "Next Vote", order = 4)
    public String getNextVote(Account account) {
        return "Available";
    }
}
