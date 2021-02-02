package com.cryo.utils;

import com.cryo.ConnectionManager;
import com.cryo.Website;
import com.cryo.entities.annotations.WebStart;
import com.cryo.entities.logs.Death;
import com.cryo.entities.logs.Duel;
import com.cryo.entities.logs.PVP;
import com.cryo.entities.logs.Trade;

import java.util.ArrayList;
import java.util.Properties;

public class Tool {

    public static int NO_RANGED = 0, NO_MELEE = 1, NO_MAGIC = 2, NO_DRINKS = 3, NO_FOOD = 4, NO_PRAYER = 5,
            OBSTACLES = 6, NO_FORFEIT = 7, FUN_WEAPON = 8, NO_SPEC = 9, NO_HELM = 10, NO_CAPE = 11, NO_AMULET = 12,
            NO_WEAPON = 13, NO_BODY = 14, NO_SHIELD = 15, NO_LEGS = 17, NO_GLOVES = 19, NO_BOOTS = 20, NO_RING = 22,
            NO_ARROWS = 23, SUMMONING = 24, NO_MOVEMENT = 25;

    public static int[] RULES = {
        NO_RANGED, NO_MELEE, NO_MAGIC, NO_DRINKS, NO_FOOD, NO_PRAYER, OBSTACLES, NO_FORFEIT, FUN_WEAPON, NO_SPEC, NO_HELM,
        NO_CAPE, NO_AMULET, NO_WEAPON, NO_BODY, NO_SHIELD, NO_LEGS, NO_GLOVES, NO_BOOTS, NO_RING, NO_ARROWS, SUMMONING, NO_MOVEMENT
    };

    public static String[] RULE_NAMES = {
            "NO_RANGED", "NO_MELEE", "NO_MAGIC", "NO_DRINKS", "NO_FOOD", "NO_PRAYER", "OBSTACLES", "NO_FORFEIT", "FUN_WEAPON", "NO_SPEC", "NO_HELM",
            "NO_CAPE", "NO_AMULET", "NO_WEAPON", "NO_BODY", "NO_SHIELD", "NO_LEGS", "NO_GLOVES", "NO_BOOTS", "NO_RING", "NO_ARROWS", "SUMMONING", "NO_MOVEMENT"
    };

    public static int getRandomRules() {
        int value = 0;
        for(int i = 0; i < 26; i++) {
            if(Utilities.random(2) == 1)
                value |= 1 << i;
            else
                value &= ~(1 << i);
        }
        return value;
    }

    public static void main2(String[] args) {
        int value = 0;
        for(int i = 0; i < 26; i++) {
            value |= 1 << i;
        }
        System.out.println(value);
        System.out.println(value & (1 << NO_BODY));
        System.out.println(value & (1 << NO_LEGS));
    }

    public static void main(String[] args) {
        Website.buildGson();
        Website.loadProperties();
        ConnectionManager connectionManager = new ConnectionManager();
        for(int i = 0; i < 10; i++) {
            ArrayList<Properties> duelerStake = new ArrayList<>();
            ArrayList<Properties> dueleeStake = new ArrayList<>();
            for(int k = 0; k < 28; k ++) {
                int id = Utilities.random(2000);
                int amount = Utilities.random(1_000_000_000, Integer.MAX_VALUE);
                String uid = Utilities.generateRandomString(15);
                Properties prop = new Properties();
                prop.put("id", id);
                prop.put("amount", amount);
                prop.put("uid", uid);
                if(k < 14)
                    duelerStake.add(prop);
                else
                    dueleeStake.add(prop);
            }
            String winner = Utilities.random(2) == 1 ? "cody" : "test";
            Duel duel = new Duel(-1, "cody", "test", Website.getGson().toJson(duelerStake), Website.getGson().toJson(dueleeStake), winner, "127.0.0.1", "127.0.0.1", getRandomRules(), null);
            connectionManager.getConnection("cryogen_logs").insert("duel", duel.data());
        }
    }
}
