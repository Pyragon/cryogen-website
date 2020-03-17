package com.cryo.entities.forums;

import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.MySQLDao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public abstract class ForumParent extends MySQLDao {

    protected ForumParent parent;

    protected int permissionsId;

    public ForumParent(int permissionsId) {
        this.permissionsId = permissionsId;
    }

    public Permissions getPermissions() {
        if(permissionsId == -1) {
            if(parent == null)
                return ForumConnection.connection().selectClass("permissions", "id=?", Permissions.class, 2);
            return parent.getPermissions();
        }
        return ForumConnection.connection().selectClass("permissions", "id=?", Permissions.class, permissionsId);
    }

}