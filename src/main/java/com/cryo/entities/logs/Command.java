package com.cryo.entities.logs;

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
public class Command extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue("ID")
    private final int id;

    @ListValue(value = "User", formatAsUser = true)
    private final String username;

    @Filterable("Command")
    @ListValue("Command")
    private final String command;

    @ListValue("Command")
    private final String parameters;

    @Filterable("IP")
    @ListValue("IP")
    private final String ip;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", formatAsTimestamp = true)
    private final Timestamp added;

    public Account getUser() {
        return AccountUtils.getAccount(username);
    }
}
