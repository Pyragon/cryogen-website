package com.cryo.modules.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cryo.db.DatabaseConnection;
import com.cryo.modules.account.entities.Punishment;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 03, 2017 at 9:32:35 AM
 */
@RequiredArgsConstructor
public abstract class Filter {
	
	public final @Getter String name;
	protected @Getter Object value;
	
	public abstract String getFilter(String mod);
	
	public abstract boolean setValue(String mod, String value);
	
	public abstract boolean appliesTo(String mod, boolean archived);
	
	public List<?> filterList(List<?> list) {
		return list;
	}
	
	public boolean isMod(String mod, String... mods) {
		return Arrays.stream(mods).anyMatch(mod::equals);
	}
	
}
