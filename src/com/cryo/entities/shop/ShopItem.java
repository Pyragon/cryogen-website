package com.cryo.entities.shop;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Data
public class ShopItem extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final int price;
    private final String name;
    private final String imageName;
    private final String type;
    private final String description;
    private final boolean active;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;
}
