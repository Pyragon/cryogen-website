package com.cryo.entities.shop;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Data
public class Package extends MySQLDao {

    @MySQLDefault
    private final int id;
    private final String username;
    private final int packageId;
    private final String invoiceId;
    private final boolean active;
    @MySQLDefault
    private final Timestamp added;
    private final Timestamp redeemed;
}
