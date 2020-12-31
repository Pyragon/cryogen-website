package com.cryo.entities.accounts;

import com.cryo.entities.MySQLDao;
import com.cryo.modules.accounts.AccountUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.cryo.Website.getConnection;

@RequiredArgsConstructor
@Data
public class HSData extends MySQLDao {

    private final int id;
    private final String username;
    private final int totalLevel;
    private final long totalXp;

    private final double[] xp;
    private final Timestamp[] stamps;

    public int getOverallRank() {
        return 1;
    }

    public Account getUser() {
        return AccountUtils.getAccount(username);
    }

    public static HSData loadClass(ResultSet set) {
        try {
            int id = set.getInt("id");
            String username = set.getString("username");
            int totalLevel = set.getInt("total_level");
            long totalXp = set.getLong("total_xp");
            double[] xp = new double[25];
            Timestamp[] stamps = new Timestamp[25];
            for(int i = 0; i < 25; i++)
                xp[i] = set.getInt("skill_"+id);
            for(int i = 0; i < 25; i++)
                stamps[i] = set.getTimestamp("skill_"+i+"_stamp");
            return new HSData(id, username, totalLevel, totalXp, xp, stamps);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
