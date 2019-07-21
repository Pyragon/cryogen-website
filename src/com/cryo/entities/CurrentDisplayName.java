package com.cryo.entities;

import lombok.Data;

@Data
public class CurrentDisplayName extends MySQLDao {

    private final String username;
    private final String displayName;

}
