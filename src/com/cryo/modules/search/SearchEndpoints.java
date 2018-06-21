package com.cryo.modules.search;

import java.util.Optional;
import java.util.stream.Stream;

import com.cryo.db.DatabaseConnection;
import com.cryo.db.impl.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SearchEndpoints {
	
	ACCOUNT_REPORTS("reports", "reports", 0, ReportsConnection.connection(), "./source/modules/account/sections/reports/reports_list.jade"),
	ACCOUNT_PUNISHMENTS("punishments", "punishments", 0, PunishmentsConnection.connection(), "./source/modules/account/sections/punishments/punishments_list.jade"),
	ACCOUNT_SHOP("shop", "shopItems", 0, ShopConnection.connection(), "./source/modules/account/sections/shop/shop_list.jade"),
	STAFF_ANNOUNCEMENTS("announcements", "announcements", 1, GlobalConnection.connection(), "./source/modules/staff/sections/announcements/announcement_list.jade"),
	STAFF_REPORTS("staff-reports", "reports", 2, ReportsConnection.connection(), "./source/modules/account/sections/reports/reports_list.jade"),
	STAFF_PUNISHMENTS("staff-punishments", "punishments", 2, PunishmentsConnection.connection(), "./source/modules/staff/sections/punishments/punishments_list.jade"),
	APPEALS("staff-appeals", "appeals", 2, PunishmentsConnection.connection(), "./source/modules/staff/sections/appeals/appeals_list.jade"),
	RECOVERIES("staff-recoveries", "recoveries", 2, RecoveryConnection.connection(), "./source/modules/staff/sections/recoveries/recoveries_list.jade");
	
	private @Getter String name, key;
	private @Getter int rights;
	private @Getter DatabaseConnection connection;
	private @Getter String jadeFile;
	
	public static Optional<SearchEndpoints> getEndpoint(String name) {
		return Stream.of(SearchEndpoints.values()).filter(e -> e.getName().equals(name)).findAny();
	}

}
