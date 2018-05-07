package com.cryo.modules.search;

import java.util.Optional;
import java.util.stream.Stream;

import com.cryo.db.DatabaseConnection;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.db.impl.PunishmentsConnection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SearchEndpoints {
	
	ACCOUNT_REPORTS("reports", "reports", ReportsConnection.connection(), "./source/modules/account/sections/reports/reports_list.jade"),
	ACCOUNT_PUNISHMENTS("punishments", "punishments", PunishmentsConnection.connection(), "./source/modules/account/sections/punishments/punishments_list.jade");
	
	private @Getter String name, key;
	private @Getter DatabaseConnection connection;
	private @Getter String jadeFile;
	
	public static Optional<SearchEndpoints> getEndpoint(String name) {
		return Stream.of(SearchEndpoints.values()).filter(e -> e.getName().equals(name)).findAny();
	}

}
