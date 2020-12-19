package com.cryo.entities.shop;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.MySQLRead;
import com.cryo.modules.account.entities.ShopItem;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Data
public class ShoppingCart extends MySQLDao {

    public static HashMap<Integer, ShopItem> cached;

    @MySQLDefault
    private final int id;
    private final String username;
    @MySQLRead
    private String items;

    public HashMap<String, String> getItems() {
        return Website.getGson().fromJson(items, HashMap.class);
    }

    @Override
    public Object[] data() {
        return new Object[] { id, username, Website.getGson().toJson(items) };
    }

    public static void loadShopItems() {
        cached = new HashMap<>();
        ArrayList<ShopItem> list = Website.getConnection("cryogen_shop").selectList("item_data", "active=1", ShopItem.class);
        list.forEach(i -> cached.put(i.getId(), i));
    }

    public static ShopItem getShopItem(int id) {
        return cached.get(id);
    }

    public static Object[] getShopItems(String filter, int page) {
        HashMap<Integer, ShopItem> items = new HashMap<>();
        if(filter == null || filter.equals("All")) {
            if(page == -1)
                return new Object[] { cached, cached.size() };
            cached.values().stream().skip((page-1)*9).limit(9).forEach(s -> items.put(s.getId(), s));
            return new Object[] { items, cached.size() };
        }
        Supplier<Stream<ShopItem>> supplier = () -> cached.values().stream().filter(s -> s.getType().equals(filter));
        int count = (int) supplier.get().count();
        if(page > 0)
            supplier.get().skip((page-1)*9).limit(9).forEach(s -> items.put(s.getId(), s));
        else
            supplier.get().forEach(s -> items.put(s.getId(), s));
        return new Object[] { items, count };
    }
}
