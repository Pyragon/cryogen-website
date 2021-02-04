package com.cryo.utils;

import com.cryo.ConnectionManager;
import com.cryo.Website;
import com.cryo.entities.annotations.WebStart;
import com.cryo.entities.logs.*;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Properties;

import static com.cryo.Website.getConnection;

public class Tool {

    public static void main(String[] args) {
        try {
            Website.buildGson();
            Website.loadProperties();
            ConnectionManager connectionManager = new ConnectionManager();
            int id = 1540;
            int amount = 1;
            String uid = Utilities.generateRandomString(15);

            //ITEMS
            Item item = new Item(-1, id, amount, uid, null);
            connectionManager.getConnection("cryogen_logs").insert("items", item.data());
            Thread.sleep(1000);

            //DROP FROM NPC TO CODY
            //18471
            NPCDrop drop = new NPCDrop(-1, "cody", 18471, 1540, 1, uid, "3333,3333,0", null);
            connectionManager.getConnection("cryogen_logs").insert("npc_drop", drop.data());
            Thread.sleep(1000);

            //TRADE FROM CODY TO TEST
            LinkedTreeMap<String, Object> traded = new LinkedTreeMap<>();
            traded.put("id", id);
            traded.put("amount", amount);
            traded.put("uid", uid);
            ArrayList<LinkedTreeMap<String, Object>> empty = new ArrayList<>();
            ArrayList<LinkedTreeMap<String, Object>> tradeItems = new ArrayList<>();
            tradeItems.add(traded);
            Trade trade = new Trade(-1, "cody", "test", Website.getGson().toJson(tradeItems), Website.getGson().toJson(empty), "127.0.0.1", "127.0.0.1", null);
            connectionManager.getConnection("cryogen_logs").insert("trade", trade.data());
            Thread.sleep(1000);

            //CODY KILL TEST
            PVP pvp = new PVP(-1, "test", "cody", "3333,3333,0", Website.getGson().toJson(tradeItems), "127.0.0.1", "127.0.0.1", null);
            connectionManager.getConnection("cryogen_logs").insert("pvp", pvp.data());
            Thread.sleep(1000);

            //CODY PICKUP ITEM
            Pickup pickup = new Pickup(-1, "cody", id, amount, uid, "3333,3333,0", "127.0.0.1", null);
            connectionManager.getConnection("cryogen_logs").insert("pickup", pickup.data());
            Thread.sleep(1000);

            //CODY SELL TO SHOP
            ShopAction action = new ShopAction(-1, "cody", 1, 1, id, amount, 1000, uid, 1, "127.0.0.1", null);
            connectionManager.getConnection("cryogen_logs").insert("shop", action.data());
            Thread.sleep(1000);

            //TEST BUY FROM SHOP
            action = new ShopAction(-1, "test", 1, 1, id, amount, 1000, uid, 0, "127.0.0.1", null);
            connectionManager.getConnection("cryogen_logs").insert("shop", action.data());
            Thread.sleep(1000);

            //TEST DIE TO BANDOS
            Death death = new Death(-1, "test", "3333,3333,0", 1, Website.getGson().toJson(tradeItems), null);
            connectionManager.getConnection("cryogen_logs").insert("death", death.data());
            Thread.sleep(1000);

            System.out.println("UID: " + uid);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
