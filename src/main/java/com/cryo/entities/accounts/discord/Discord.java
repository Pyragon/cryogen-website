package com.cryo.entities.accounts.discord;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;
import net.dv8tion.jda.api.entities.User;

import java.sql.Timestamp;

@Data
public class Discord extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String username;
    private final long discordId;
    @MySQLDefault
    private final Timestamp added;

    public String getIdString() {
        User user = Website.getJDA().retrieveUserById(discordId).complete();
        if(user == null) return "";
        return user.getAsTag();
    }
}
