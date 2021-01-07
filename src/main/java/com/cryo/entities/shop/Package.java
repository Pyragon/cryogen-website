package com.cryo.entities.shop;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.list.Filterable;
import com.cryo.entities.list.ListValue;
import com.cryo.entities.list.SortAndFilter;
import com.cryo.entities.list.Sortable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

import static com.cryo.Website.getConnection;

@Data
@RequiredArgsConstructor
public class Package extends MySQLDao {

    @MySQLDefault
    @Sortable("ID")
    @ListValue(value = "ID", order = 0)
    private final int id;
    private final String username;

    @Sortable("Package ID")
    @ListValue(value = "Package ID", order = 1)
    private final int packageId;

    @SortAndFilter(value = "Invoice ID")
    @ListValue(value = "Invoice ID", order = 4)
    private final String invoiceId;

    private final boolean active;

    @Sortable(value = "Redeemed", onArchive = true)
    @ListValue(value = "Redeemed", onArchive = true, formatAsTimestamp = true, order = 6)
    private final Timestamp redeemDate;

    @MySQLDefault
    @Sortable("Purchased")
    @ListValue(value = "Purchased", formatAsTimestamp = true, order = 5)
    private final Timestamp added;

    @MySQLDefault
    private final Timestamp updated;

    @ListValue(value = "Redeem", className = "redeem-btn", order = 7, notOnArchive = true, isButton = true)
    private Object redeemButton = "Redeem";

    @Filterable("Package Name")
    @ListValue(value = "Package Name", order = 2)
    public String getPackageName() {
        ShopItem item = getConnection("cryogen_shop").selectClass("item_data", "id=?", ShopItem.class, packageId);
        return item == null ? "N/A" : item.getName();
    }

    @Filterable("Package Type")
    @ListValue(value = "Package Type", order = 3)
    public String getPackageType() {
        ShopItem item = getConnection("cryogen_shop").selectClass("item_data", "id=?", ShopItem.class, packageId);
        return item == null ? "N/A" : item.getType();
    }
}
