package com.cryo.entities.shop;

import com.cryo.Website;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.ConnectionUtils.*;

@Data
@AllArgsConstructor
public class ShoppingCart {

    @MySQLDefault
    @MySQLRead
    private int id;
    private final String username;
    private HashMap<String, Double> items;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public int getTotalQuantity() {
        try {
            if (items == null) return 0;
            int total = 0;
            for (String key : items.keySet())
                total += items.get(key);
            return total;
        } catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getTotalPrice() {
        if(items == null) return 0;
        double price = 0;
        for(String id : items.keySet()) {
            ShopItem item = ShopItem.items.get(Integer.parseInt(id));
            if(item == null) continue;
            price += item.getPrice()*items.get(id);
        }
        return price;
    }

    public int getQuantity(int itemId) {
        return getQuantity(Integer.toString(itemId));
    }

    public int getQuantity(String itemId) {
        if(items == null) return 0;
        if(!items.containsKey(itemId)) return 0;
        return (int) Math.floor(items.get(itemId));
    }

    public static ShoppingCart loadClass(ResultSet set) {
        try {
            int id = getInt(set, "id");
            String username = getString(set, "username");
            String itemsString = getString(set, "items");
            Timestamp added = getTimestamp(set, "added");
            Timestamp updated = getTimestamp(set, "updated");
            HashMap<String, Double> items;
            if(itemsString == null || itemsString.equals(""))
                items = new HashMap<>();
            else
                items = Website.getGson().fromJson(itemsString, HashMap.class);
            return new ShoppingCart(id, username, items, added, updated);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save() {
        getConnection("cryogen_shop").set("cart_data", "items=?", "id=?", Website.getGson().toJson(items), id);
    }

    public static ShoppingCart getCart(String username) {
        ShoppingCart cart = getConnection("cryogen_shop").selectClass("cart_data", "username=?", ShoppingCart.class, username);
        if(cart == null)
            cart = createCart(username);
        return cart;
    }

    public static ShoppingCart createCart(String username) {
        ShoppingCart cart = new ShoppingCart(-1, username, new HashMap<>(), null, null);
        int id = getConnection("cryogen_shop").insert("cart_data", cart.data());
        cart.setId(id);
        return cart;
    }

    public Object[] data() {
        return new Object[] { "DEFAULT", username, Website.getGson().toJson(items), "DEFAULT", "DEFAULT" };
    }
}
