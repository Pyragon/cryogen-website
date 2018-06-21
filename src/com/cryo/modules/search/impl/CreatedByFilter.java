package com.cryo.modules.search.impl;

import com.cryo.modules.search.Filter;

public class CreatedByFilter extends Filter {

	public CreatedByFilter() {
		super("createdby");
	}

	@Override
	public String getFilter(String mod) {
		return "username LIKE ?";
	}

	@Override
	public boolean setValue(String mod, String value) {
		value = value.toLowerCase();
		this.value = "%"+value+"%";
		return true;
	}

	@Override
	public boolean appliesTo(String mod, boolean archived) {
		return isMod(mod, "announcements");
	}

}
