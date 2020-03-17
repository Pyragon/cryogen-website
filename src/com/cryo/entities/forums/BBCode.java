package com.cryo.entities.forums;

import java.sql.Timestamp;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BBCode extends MySQLDao {

    private final int id;
    private final String name;
    private final String description;
    private final String regex;
    private final String replacement;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

}