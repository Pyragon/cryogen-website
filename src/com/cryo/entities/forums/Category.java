package com.cryo.entities.forums;

import com.cryo.Website;
import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Category extends ForumParent {

    @MySQLDefault
    private final int id;
    private final String name;
    private final String description;
    private final int permissionsId;
    private final int priority;
    @MySQLDefault
    private final Timestamp updated;
    @MySQLDefault
    private final Timestamp added;

    public Category(int id, String name, String description, int permissions, int priority, Timestamp updated, Timestamp added) {
        super(permissions);
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissionsId = permissions;
        this.priority = priority;
        this.updated = updated;
        this.added = added;
    }

    public ArrayList<SubForum> getSubForums() {
        Object data = Website.instance().getCachingManager().getData("subforum-list-cache", true, id);
        if(data == null || !(data instanceof ArrayList)) return null;
        return (ArrayList<SubForum>) data;
    }

}
