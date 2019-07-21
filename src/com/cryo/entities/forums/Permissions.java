package com.cryo.entities.forums;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

@Data
public class Permissions extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int type;
    private final int canReadThread;
}
