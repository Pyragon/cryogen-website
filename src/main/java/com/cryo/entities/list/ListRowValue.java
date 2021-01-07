package com.cryo.entities.list;

import com.cryo.entities.accounts.Account;
import com.cryo.modules.account.AccountUtils;
import lombok.Data;

@Data
public class ListRowValue {

    private final Object value;

    private int order;

    private boolean shouldFormatAsTime;
    private boolean shouldFormatAsTimestamp;
    private boolean shouldFormatAsNumber;
    private boolean shouldFormatAsUser;

    private String className;

    private boolean isButton;

    public boolean shouldFormatAsTime() {
        return shouldFormatAsTime;
    }

    public boolean shouldFormatAsTimestamp() {
        return shouldFormatAsTimestamp;
    }

    public boolean shouldFormatAsNumber() {
        return shouldFormatAsNumber;
    }

    public boolean shouldFormatAsUser() {
        return shouldFormatAsUser;
    }

    public boolean isButton() { return isButton; }

    public Account getUser() {
        if(value instanceof Integer)
            return AccountUtils.getAccount((Integer) value);
        if(value instanceof String)
            return AccountUtils.getAccount((String) value);
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof ListRowValue)) return false;
        return ((ListRowValue) object).value == value;
    }
}
