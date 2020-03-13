package com.cryo.tasks.impl;

import com.cryo.Website;
import com.cryo.db.impl.AccountConnection;

@SuppressWarnings("unused")
public class RemoveExpiredTokensTask extends Task {

    public RemoveExpiredTokensTask() {
        super("* * %5 *");
    }

    @Override
    public void run() {
        if(!Website.LOADED || AccountConnection.connection() == null) return;
        AccountConnection.connection().handleRequest("remove-tokens");
    }
}
