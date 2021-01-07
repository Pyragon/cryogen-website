package com.cryo.entities.list;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListRow {

    private final int id;
    private List<ListRowValue> values = new ArrayList<>();
}
