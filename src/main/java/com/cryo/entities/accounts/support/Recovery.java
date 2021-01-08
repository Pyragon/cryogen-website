package com.cryo.entities.accounts.support;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Recovery extends MySQLDao {

    @MySQLDefault
    private final int id;

    private final String username;

    private final String viewKey;

    private final String emailKey;

    private final String discordKey;

    private final int emailStatus;
    private final String emailEntered;

    private final int discordStatus;
    private final String discordEntered;

    private final boolean creationDateSet;
    private final int creationDateDifference;

    private final String cityCountry;

    private final String isp;

    //-1 = nothing entered, 0 = entered but incorrect, 1 = entered and correct
    private ArrayList<Integer> previousPasswordStatuses;
    private final String previousPasswordString;

    private final int correctRecoveryQuestions;

    private final String additional;

    private final int status;
    @MySQLDefault
    private final String decider;
    @MySQLDefault
    private final String newPassword;
    @MySQLDefault
    private final String reason;
    @MySQLDefault
    private final Timestamp decided;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public ArrayList<Integer> getPreviousPasswordStatuses() {
        if(previousPasswordStatuses == null)
            previousPasswordStatuses = Website.getGson().fromJson(previousPasswordString, ArrayList.class);
        return previousPasswordStatuses;
    }

}
