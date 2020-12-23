package com.cryo.entities.forums;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.modules.forums.ForumModule;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserGroup extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String name;
    private final int rights;
    private final String colour;
    private final String imageBefore;
    private final String imageAfter;
    private final String userTitle;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public String getFormattedName() {
        return ForumModule.crownUser(this, 14, 15);
    }

}
