package com.cryo.entities.accounts;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class PreviousPassList extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String username;
    private final ArrayList<String> hashes;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public static PreviousPassList loadClass(ResultSet set) {
        try {
            int id = set.getInt("id");
            String username = set.getString("username");
            ArrayList<String> hashes = Website.getGson().fromJson(set.getString("hashes"), ArrayList.class);
            Timestamp added = set.getTimestamp("added");
            Timestamp updated = set.getTimestamp("updated");
            return new PreviousPassList(id, username, hashes, added, updated);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
