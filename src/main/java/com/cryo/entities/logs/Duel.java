package com.cryo.entities.logs;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import com.cryo.modules.account.AccountUtils;
import com.cryo.utils.Utilities;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Duel extends MySQLDao {

    public static int NO_RANGED = 0, NO_MELEE = 1, NO_MAGIC = 2, NO_DRINKS = 3, NO_FOOD = 4, NO_PRAYER = 5,
            OBSTACLES = 6, NO_FORFEIT = 7, FUN_WEAPON = 8, NO_SPEC = 9, NO_HELM = 10, NO_CAPE = 11, NO_AMULET = 12,
            NO_WEAPON = 13, NO_BODY = 14, NO_SHIELD = 15, NO_LEGS = 17, NO_GLOVES = 19, NO_BOOTS = 20, NO_RING = 22,
            NO_ARROWS = 23, SUMMONING = 24, NO_MOVEMENT = 25;

    public static String[] RULE_NAMES = {
            "NO_RANGED", "NO_MELEE", "NO_MAGIC", "NO_DRINKS", "NO_FOOD", "NO_PRAYER", "OBSTACLES", "NO_FORFEIT", "FUN_WEAPON", "NO_SPEC", "NO_HELM",
            "NO_CAPE", "NO_AMULET", "NO_WEAPON", "NO_BODY", "NO_SHIELD", "", "NO_LEGS", "", "NO_GLOVES", "NO_BOOTS", "", "NO_RING", "NO_ARROWS", "SUMMONING", "NO_MOVEMENT"
    };

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;

    @Filterable("Dueler")
    @ListValue(value = "Dueler", order = 1, formatAsUser = true)
    private final String dueler;

    @Filterable("Duelee")
    @ListValue(value = "Duelee", order = 2, formatAsUser = true)
    private final String duelee;

    private final String duelerStake;
    private final String dueleeStake;

    @Filterable("Winner")
    @ListValue(value = "Winner", order = 3, formatAsUser = true)
    private final String winner;

    @Filterable("Dueler IP")
    @ListValue(value = "Dueler IP", order = 6)
    private final String duelerIp;

    @Filterable("Duelee IP")
    @ListValue(value = "Duelee IP", order = 6)
    private final String dueleeIp;

    private final int rules;

    @MySQLDefault
    @SortAndFilter("Added")
    @ListValue(value = "Added", order = 7, formatAsTimestamp = true)
    private final Timestamp added;

    @ListValue(value = "View", order = 8, isButton = true, className = "view-duel")
    private Object view = "View";

    public Account getDueler() {
        return AccountUtils.getAccount(dueler);
    }

    public Account getDuelee() {
        return AccountUtils.getAccount(duelee);
    }

    public Account getWinner() {
        return AccountUtils.getAccount(winner);
    }

    public ArrayList<LinkedTreeMap<String, Object>> getDuelerStake() {
        return Website.getGson().fromJson(duelerStake, ArrayList.class);
    }

    public ArrayList<LinkedTreeMap<String, Object>> getDueleeStake() {
        return Website.getGson().fromJson(dueleeStake, ArrayList.class);
    }

    @ListValue(value = "Stake", order = 4)
    public String getStakeString() {
        return getDuelerStake().size()+" <-> "+getDueleeStake().size();
    }

    @ListValue(value = "Rules Active", order = 5)
    public int getRulesActive() {
        int active = 0;
        for(int i = 0; i < 26; i++) {
            if(i == 16 || i == 18 || i == 21) continue;
            if (isActive(i))
                active++;
        }
        return active;
    }

    public boolean isActive(int rule) {
        return (rules & (1 << rule)) != 0;
    }

    public ArrayList<DuelRule> getRules() {
        ArrayList<DuelRule> rules = new ArrayList<>();
        for(int i = 0; i < 26; i++) {
            if(i == 16 || i == 18 || i == 21) continue;
            String name = Utilities.formatNameForDisplay(RULE_NAMES[i]);
            boolean active = isActive(i);
            rules.add(new DuelRule(name, active));
        }
        return rules;
    }
}
