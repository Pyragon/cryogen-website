package com.cryo.tasks.impl;

import com.cryo.db.impl.ForumConnection;

public class RemoveExpiredStatuses extends Task {

    public RemoveExpiredStatuses() {
        super("* * %5 *");
    }

    @Override
    public void run() {
        ForumConnection.connection().delete("account_statuses", "expiry < CURRENT_TIMESTAMP()");
    }

}