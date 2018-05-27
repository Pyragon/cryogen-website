package com.cryo.modules.search.impl;

import com.cryo.modules.search.Filter;

public class NameFilter extends Filter {

	public NameFilter() {
		super("name");
	}

	@Override
	public String getFilter(String mod) {
		return "name LIKE ?";
	}

	@Override
	public boolean setValue(String mod, String value) {
		value = value.toLowerCase();
		this.value = "%"+value+"%";
		return true;
	}

	@Override
	public boolean appliesTo(String mod) {
		return mod.equals("shop");
	}

}
