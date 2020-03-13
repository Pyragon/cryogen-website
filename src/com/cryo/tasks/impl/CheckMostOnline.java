package com.cryo.tasks.impl;

import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.Misc;

public class CheckMostOnline extends Task {

    public CheckMostOnline() {
        super("* * * %5");
    }

    public void run() {
        int online = ForumConnection.connection().selectCount("account_statuses", "expiry > CURRENT_TIMESTAMP()");
        Misc misc = GlobalConnection.connection().selectClass("misc_data", "name=?", Misc.class, "most_online");
        int mostOnline = 0;
        if(misc != null) mostOnline = misc.asInt();
        if(online > mostOnline) {
            if(misc != null)
                GlobalConnection.connection().set("misc_data", "value=?", "name=?", online, "most_online");
            else {
                misc = new Misc("most_online", online);
                GlobalConnection.connection().insert("misc_data", misc.data());
            }
        }
    }

}