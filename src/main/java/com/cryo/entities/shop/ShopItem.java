package com.cryo.entities.shop;

import com.cryo.Website;
import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import static com.cryo.Website.getConnection;

@Data
public class ShopItem extends MySQLDao {

    public static HashMap<Integer, ShopItem> items;

    static {

        items = new HashMap<>();

        List<ShopItem> items = getConnection("cryogen_shop").selectList("item_data", ShopItem.class);
        items.forEach(i -> ShopItem.items.put(i.getId(), i));

    }

    @MySQLDefault
    private final int id;
    private final double price;
    private final String name;
    private final String imageName;
    private final String type;
    private final String description;
    private final boolean active;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    public String getImageLink() {
        return Website.getProperties().getProperty("path")+"images/shop/"+imageName;
    }

}
