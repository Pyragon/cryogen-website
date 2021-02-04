package com.cryo.modules.logs;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.logs.PVP;
import com.cryo.entities.logs.*;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.utils.FormatUtils;
import com.cryo.utils.Utilities;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.util.*;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class ItemTracking {

    @Endpoint(method = "POST", endpoint = "/logs/items/load")
    public static String renderItemTrackingLogs(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return com.cryo.modules.account.Login.renderLoginPage("/logs/items", request, response);
        if(account.getRights() < 2) return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
        return renderPage("logs/views/items/index", null, request, response);
    }

    @SuppressWarnings("unchecked")
    @Endpoint(method = "POST", endpoint = "/logs/items/table")
    public static String renderTable(Request request, Response response) {
        try {
            Account account = AccountUtils.getAccount(request);
            if (account == null) return error("Session has expired. Please refresh the page and try again.");
            if (account.getRights() < 2)
                return Utilities.redirect("/", "Invalid permissions", null, null, request, response);
            HashMap<String, Object> model = new HashMap<>();
            model.put("moduleId", "items");
            ArrayList<ArrayList<Object>> sortValues = new ArrayList<>();
            if (request.queryParams().contains("sortValues"))
                sortValues = Website.getGson().fromJson(request.queryParams("sortValues"), ArrayList.class);
            ArrayList<ArrayList<Object>> filterValues = new ArrayList<>();
            if (request.queryParams().contains("filterValues"))
                filterValues = Website.getGson().fromJson(request.queryParams("filterValues"), ArrayList.class);
            if (!request.queryParams().contains("page") || !NumberUtils.isDigits(request.queryParams("page")))
                return error("Unable to parse page number. Please refresh the page and try again.");
            if (!request.queryParams().contains("uid"))
                return error("Unable to find UID. Please refresh the page and try again.");
            String uid = request.queryParams("uid");
            Item item = getConnection("cryogen_logs").selectClass("items", "uid=?", Item.class, uid);
            if (item == null)
                return error("Unable to find UID. Please refresh the page and try again.");
            String uidWild = "%" + uid + "%";
            int page = Integer.parseInt(request.queryParams("page"));

            List<TrackedItem> items = new ArrayList<>();

            //TRADES
            List<Trade> trades = getConnection("cryogen_logs").selectList("trade", "trader_items LIKE ? OR tradee_items LIKE ?", Trade.class, uidWild, uidWild);
            for (Trade trade : trades) {
                boolean found = false;
                String trader = "";
                String tradee = "";
                for (LinkedTreeMap<String, Object> traderItems : trade.getTraderItems()) {
                    if (traderItems.get("uid").equals(uid)) {
                        found = true;
                        trader = trade.getTrader().getUsername();
                        tradee = trade.getTradee().getUsername();
                    }
                }
                if (!found) {
                    trader = trade.getTradee().getUsername();
                    tradee = trade.getTrader().getUsername();
                }
                items.add(new TrackedItem(trade.getId(), "Trade", trader, "Traded to", tradee, null, trade.getAdded()));
            }

            //DEATHS
            List<Death> deaths = getConnection("cryogen_logs").selectList("death", "items_lost LIKE ?", Death.class, uidWild);
            for (Death death : deaths)
                items.add(new TrackedItem(death.getId(), "Death", death.getUsername(), "Died to", death.getKilledBy(), death.getWorldTile(), death.getAdded()));

            //DROPPED
            List<Drop> drops = getConnection("cryogen_logs").selectList("`drop`", "uid=?", Drop.class, uid);
            for (Drop drop : drops)
                items.add(new TrackedItem(drop.getId(), "Drop", drop.getUsername(), drop.getDropType() + " Item", null, drop.getWorldTile(), drop.getAdded()));

            //DUELS
            List<Duel> duels = getConnection("cryogen_logs").selectList("duel", "dueler_stake LIKE ? OR duelee_stake LIKE ?", Duel.class, uidWild, uidWild);
            for (Duel duel : duels) {
                String winner = duel.getWinner().getUsername();
                String dueler = duel.getDueler().getUsername();
                ArrayList<LinkedTreeMap<String, Object>> losingStake;
                if (winner.equals(dueler))
                    losingStake = duel.getDueleeStake();
                else
                    losingStake = duel.getDuelerStake();
                boolean found = false;
                for (LinkedTreeMap<String, Object> lost : losingStake) {
                    if (lost.get("uid").equals(uid)) {
                        found = true;
                        break;
                    }
                }
                if (!found) continue; //If it wasn't lost, who cares?
                String loser = winner.equals(dueler) ? duel.getDuelee().getUsername() : dueler;
                items.add(new TrackedItem(duel.getId(), "Duel", loser, "Lost item in duel to", winner, null, duel.getAdded()));
            }

            //PICKUP
            List<Pickup> pickups = getConnection("cryogen_logs").selectList("pickup", "uid=?", Pickup.class, uid);
            for (Pickup pickup : pickups)
                items.add(new TrackedItem(pickup.getId(), "Pickup", pickup.getUsername(), "Picked up item", null, pickup.getWorldTile(), pickup.getAdded()));

            //POS
            List<POSPurchase> pos = getConnection("cryogen_logs").selectList("pos", "uid=?", POSPurchase.class, uid);
            for (POSPurchase purchase : pos)
                items.add(new TrackedItem(purchase.getId(), "POS", purchase.getUsername(), "Purchased item @ " + FormatUtils.formatRunescapeNumber(purchase.getPrice()) + " from POS of", purchase.getOwner(), null, purchase.getAdded()));

            //PVP
            List<PVP> pvp = getConnection("cryogen_logs").selectList("pvp", "items_lost LIKE ?", PVP.class, uidWild);
            for (PVP lost : pvp)
                items.add(new TrackedItem(lost.getId(), "PVP", lost.getUsername(), "Dropped item due to death from", lost.getKiller().getUsername(), lost.getWorldTile(), lost.getAdded()));

            //SHOP
            List<ShopAction> shop = getConnection("cryogen_logs").selectList("shop", "uid=?", ShopAction.class, uid);
            for (ShopAction action : shop)
                items.add(new TrackedItem(action.getId(), "Shop", action.getUsername(), action.getTypeString() + " item " + (action.getType() == 0 ? "from" : "to") + " shop", action.getNpcId(), null, action.getAdded()));

            //NPC DROPS
            List<NPCDrop> npcDrops = getConnection("cryogen_logs").selectList("npc_drop", "uid=?", NPCDrop.class, uid);
            for(NPCDrop drop : npcDrops)
                items.add(new TrackedItem(drop.getId(), "NPC Drop", drop.getUsername(), "Received as drop from", drop.getNpcId(), drop.getWorldTile(), drop.getAdded()));

            //BOB
            List<BOB> bobs = getConnection("cryogen_logs").selectList("bob", "uid=?", BOB.class, uidWild);
            for(BOB bob : bobs)
                items.add(new TrackedItem(bob.getId(), "BOB", bob.getUsername(), bob.getType()+" from ", bob.getNpcId(), null, bob.getAdded()));

            List<BOBDeath> bobDeaths = getConnection("cryogen_logs").selectList("bob_deaths", "items_lost LIKE ?", BOBDeath.class, uidWild);
            for(BOBDeath death : bobDeaths)
                items.add(new TrackedItem(death.getId(), "BOB Death", death.getUsername(), "BOB Died", death.getNpcId(), death.getWorldTile(), death.getAdded()));

            //DICES
            List<Dice> dices = getConnection("cryogen_logs").selectList("dice", "host_stake LIKE ? OR dicer_stake LIKE ?", Dice.class, uidWild);
            for(Dice dice : dices) {
                String winner = dice.getWinner();
                ArrayList<LinkedTreeMap<String, Object>> losingStake = winner.equals(dice.getDicer()) ? dice.getHostStake() : dice.getDicerStake();
                boolean found = false;
                for (LinkedTreeMap<String, Object> lost : losingStake) {
                    if (lost.get("uid").equals(uid)) {
                        found = true;
                        break;
                    }
                }
                if (!found) continue; //If it wasn't lost, who cares?
                String loser = winner.equals(dice.getDicer()) ? dice.getHost() : dice.getDicer();
                items.add(new TrackedItem(dice.getId(), "Dice", loser, "Lost item in dice to", winner, null, dice.getAdded()));
            }

            List<GrandExchange> geLogs = getConnection("cryogen_logs").selectList("grand_exchange", "uid=?", GrandExchange.class, uid);
            for(GrandExchange ge : geLogs)
                items.add(new TrackedItem(ge.getId(), "GE", ge.getUsername(), ge.getAction(), ge.getBuyer(), null, ge.getAdded()));

            items.sort(Comparator.comparingLong(i -> i.getAdded().getTime()));
            model.put("itemId", item.getItemId());
            model.put("itemName", FormatUtils.toItemName(item.getItemId()));
            model.put("uid", item.getUid());
            ListManager.buildTable(model, "logs", items, TrackedItem.class, account, sortValues, filterValues, false);
            return renderList(model, request, response);
        } catch(Exception e) {
            e.printStackTrace();
            return error("Error loading tracked item. Please check the UID and try again.");
        }
    }

}
