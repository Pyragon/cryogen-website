package com.cryo.entities.list;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

@Data
public class SortedOrFilteredValue {

    private final String name;
    private final String value;
    private final int index;

    private int secondIndex;

    private ArrayList<Properties> values;

    public boolean equals(Object object) {
        if(!(object instanceof SortedOrFilteredValue)) return false;
        return ((SortedOrFilteredValue) object).name.equals(name);
    }
}
