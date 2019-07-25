package com.cryo.entities.forums;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Category extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String name;
    private final String description;
    private final int permissions;
    private final int priority;
    @MySQLDefault
    private final Timestamp updated;
    @MySQLDefault
    private final Timestamp added;

    public ArrayList<SubForum> getSubForums() {
        Object data = Website.instance().getCachingManager().getData("subforum-list-cache", true, id);
        if(data == null || !(data instanceof ArrayList)) return null;
        return (ArrayList<SubForum>) data;
    }

    public Permissions getPermissions() {
        Object data = Website.instance().getCachingManager().getData("permissions-cache", permissions);
        if (data == null) return null;
        return (Permissions) data;
    }

}
