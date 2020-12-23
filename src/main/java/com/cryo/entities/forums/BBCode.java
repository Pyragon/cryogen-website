package com.cryo.entities.forums;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class BBCode extends MySQLDao {

    @MySQLDefault
    private final int id;
    @MySQLRead
    private String name;
    @MySQLRead
    private String description;
    @MySQLRead(value="css")
    private String CSS;
    @MySQLRead
    private boolean allowNested;
    @MySQLRead
    private String regex;
    @MySQLRead
    private String replacement;
    @MySQLRead
    private String example;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

}