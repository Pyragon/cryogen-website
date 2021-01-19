package com.cryo.entities.accounts.support;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.filters.DiscordStatusFilter;
import com.cryo.entities.accounts.filters.EmailStatusFilter;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListRowValue;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.Sortable;
import com.cryo.modules.account.AccountUtils;
import com.mysql.cj.util.StringUtils;
import io.ipinfo.api.model.IPResponse;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Recovery extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable(value = "User", requiresModule = "staff")
    @ListValue(value = "User", order = 1, formatAsUser = true, requiresModule = "staff")
    private final String username;

    private final String viewKey;

    private final String emailKey;

    private final String discordKey;

    private final int emailStatus;
    private final String emailEntered;

    private final int discordStatus;
    private final String discordEntered;

    private final Timestamp creationDate;

    private final String cityCountry;

    @MySQLRead("isp")
    private final String ISP;

    //-1 = nothing entered, 0 = entered but incorrect, 1 = entered and correct
    private List<Integer> previousPasswordStatuses;
    @MySQLRead("previous_passwords")
    private final String previousPasswordString;

    private final int correctRecoveryQuestions;

    private final String additional;

    //1 = submitted, nothing else. 2 = denied, 3 = accepted by email/discord, 4 = accepted by staff
    private final int status;
    @MySQLDefault
    private final String decider;
    @MySQLDefault
    private final String newPassword;
    @MySQLDefault
    private final String reason;

    private final Timestamp decided;

    @MySQLDefault
    @Sortable("Added")
    @ListValue(value = "Added", order = 7, formatAsTimestamp = true)
    private final Timestamp added;

    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "View", order = 8, isButton = true, className = "view-recovery", requiresModule = "staff")
    private Object viewButton = "View";

    public List<Integer> getPreviousPasswordStatuses() {
        if (previousPasswordStatuses == null) {
            ArrayList<Double> doubles = Website.getGson().fromJson(previousPasswordString, ArrayList.class);
            previousPasswordStatuses = doubles.stream().map(d -> (int) Math.floor(d)).collect(Collectors.toList());
        }
        return previousPasswordStatuses;
    }

    public String getPreviousPasswordStatus(int index) {
        switch (getPreviousPasswordStatuses().get(index)) {
            case -1:
                return "Nothing Entered";
            case 0:
                return "Incorrect value entered";
            case 1:
                return "Correct value entered";
        }
        return "N/A";
    }

    public String getPasswordIconClass(int index) {
        switch (getPreviousPasswordStatuses().get(index)) {
            case -1:
            case 0:
                return "fa fa-times-circle-o color-red";
            case 1:
                return "fa fa-check-circle-o color-green";
        }
        return "";
    }

    @Filterable(value = "Email Status", values = EmailStatusFilter.class, dbName = "email_status")
    @ListValue(value = "Email Status", order = 2)
    public String getEmailTableStatus() {
        return emailStatus == 3 ? "Email Sent" : "Email Not Sent";
    }

    @Filterable(value = "Discord Status", values = DiscordStatusFilter.class, dbName = "discord_status")
    @ListValue(value = "Discord Status", order = 3)
    public String getDiscordTableStatus() {
        return discordStatus == 3 ? "Discord Message Sent" : "Discord Message Not Sent";
    }

    @ListValue(value = "Likeliness", order = 5, notOnArchive = true)
    public String getLikeliness() {
        return "TODO";
    }

    @ListValue(value = "Status", order = 6, onArchive = true, returnsValue = true)
    public ListRowValue getTableStatus() {
        ListRowValue value = new ListRowValue(getStatusStr());
        value.setOrder(6);
        value.setClassName(getStatusClass());
        return value;
    }

    @Sortable("Fields Entered")
    @ListValue(value = "Fields Entered", order = 4)
    public String getFieldsSet() {
        int total = 0;
        if (!StringUtils.isNullOrEmpty(emailEntered))
            total++;
        if (!StringUtils.isNullOrEmpty(discordEntered))
            total++;
        if (creationDate != null)
            total++;
        if (!StringUtils.isNullOrEmpty(cityCountry))
            total++;
        if (!StringUtils.isNullOrEmpty(ISP))
            total++;
        for (int status : getPreviousPasswordStatuses()) {
            if (status != -1)
                total++;
        }
        total += correctRecoveryQuestions;
        if (!StringUtils.isNullOrEmpty(additional))
            total++;
        return Integer.toString(total);
    }

    public String getRealCityCountry() {
        try {
            IPResponse response = Website.getIPLookup().lookupIP(getAccount().getCreationIP());
            if (response == null) return "N/A";
            return response.getCity() + ", " + response.getRegion() + ", " + response.getCountryCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public String getRealISP() {
        try {
            IPResponse response = Website.getIPLookup().lookupIP(getAccount().getCreationIP());
            if (response == null) return "N/A";
            if (response.getCarrier() != null)
                return response.getCarrier().getName();
            if (response.getAsn() != null)
                return response.getAsn().getName();
            if (response.getCompany() != null)
                return response.getCompany().getName();
            if (response.getOrg() != null)
                return response.getOrg();
            return "N/A";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public String getEDTitle(boolean email) {
        int status = email ? emailStatus : discordStatus;
        String key = email ? "an email" : "a discord";
        switch (status) {
            case -1:
                return "User entered no value. Account has value associated. Look at lists to see what is currently linked.";
            case -2:
                return "User entered no value. Account does not have a value associated.";
            case 1:
                return "User entered value. Account does have value. Entered value was incorrect.";
            case 2:
                return "User entered value. Account does not have a value associated.";
            case 3:
                return "User entered value during recovery. Value was correct. A password recovery has been sent to " + (email ? "email" : "discord") + ".";
        }
        return "";
    }

    public String getStatusClass() {
        switch (status) {
            case 1:
                return "color-yellow";
            case 2:
                return "color-red";
            case 3:
            case 4:
                return "color-green";
        }
        return "";
    }

    public String getStatusStr() {
        switch (status) {
            case 1:
                return "Submitted";
            case 2:
                return "Denied";
            case 3:
                return "Recovered via email/discord";
            case 4:
                return "Recovered via staff member";
        }
        return "N/A";
    }

    public boolean isArchived() {
        return status != 1;
    }

    public Account getAccount() {
        return AccountUtils.getAccount(username);
    }

    public Account getDecider() {
        return AccountUtils.getAccount(decider);
    }

}
