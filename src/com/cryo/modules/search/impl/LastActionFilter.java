package com.cryo.modules.search.impl;

import com.cryo.modules.search.Filter;

public class LastActionFilter extends Filter {

	public LastActionFilter() {
		super("last_action");
	}

	@Override
	public String getFilter(String mod) {
		return "last_action LIKE ?";
	}

	@Override
	public boolean setValue(String mod, String value) {
		this.value = "%"+value+"%";
		return true;
	}

	@Override
	public boolean appliesTo(String mod) {
		return mod.equals("reports");
	}

}
