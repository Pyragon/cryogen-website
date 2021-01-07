package com.cryo.entities.list;

import lombok.Data;

@Data
public class SortedOrFilteredValue {

    private final String name;
    private final String value;
    private final int index;

    public boolean equals(Object object) {
        if(!(object instanceof SortedOrFilteredValue)) return false;
        return ((SortedOrFilteredValue) object).name.equals(name);
    }
}
