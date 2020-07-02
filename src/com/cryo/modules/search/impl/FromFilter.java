package com.cryo.modules.search.impl;

import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.search.Filter;
import com.cryo.utils.DisplayNames;
import com.cryo.utils.Utilities;

public class FromFilter extends Filter {

    public FromFilter() {
        super("from");
    }

    @Override
    public String getFilter(String mod) {
        return "from_id=?";
    }

    @Override
    public boolean setValue(String mod, String value) {
        String username = DisplayNames.getUsername(Utilities.formatNameForProtocol(value));
        if(username == null) return false;
        Account account  = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, username);
        if(account == null) return false;
        this.value = account.getId();
        return true;
    }

    @Override
    public boolean appliesTo(String mod, boolean archived) {
        return mod.equals("inbox");
    }
}
