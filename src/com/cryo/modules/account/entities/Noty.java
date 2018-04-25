package com.cryo.modules.account.entities;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

public enum Noty {
	CREATE_BUG_REPORT("./source/modules/account/sections/reports/create_bug_report.jade"),
	CREATE_PLAYER_REPORT("./source/modules/account/sections/reports/create_player_report.jade"),
	VIEW_BUG_REPORT("./source/modules/account/sections/reports/view_bug_report.jade"),
	VIEW_PLAYER_REPORT("./source/modules/account/sections/reports/view_player_report.jade"),;
	
	private @Getter String file;
	
	Noty(String file) {
		this.file = file;
	}
	
	public static Optional<Noty> get(String name) {
		return Stream.of(Noty.values()).filter(n -> n.name().equalsIgnoreCase(name)).findFirst();
	}
}
