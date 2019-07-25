package com.cryo.entities.forums;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Thanks extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int postId;
    private final int authorId;
    private final int accountId;
    @MySQLDefault
    private Timestamp added;

}
