package com.cryo.tasks.impl;

import com.cryo.Website;
import com.cryo.db.impl.AccountConnection;

@SuppressWarnings("unused")
public class RemoveExpiredTokensTask extends Task {

    public RemoveExpiredTokensTask() {
        super(-1, -1, -1);
    }

    @Override
    public void run() {
        if(!Website.LOADED) return;
        AccountConnection.connection().handleRequest("remove-tokens");
    }
}
