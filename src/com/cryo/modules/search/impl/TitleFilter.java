package com.cryo.modules.search.impl;

import com.cryo.modules.search.Filter;

public class TitleFilter extends Filter {

	public TitleFilter() {
		super("title");
	}

	@Override
	public String getFilter(String mod) {
		return "title LIKE ?";
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
