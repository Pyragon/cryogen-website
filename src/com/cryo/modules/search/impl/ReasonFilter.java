package com.cryo.modules.search.impl;

import com.cryo.modules.search.Filter;

public class ReasonFilter extends Filter {

	public ReasonFilter() {
		super("reason");
	}

	@Override
	public String getFilter(String mod) {
		return "reason LIKE ?";
	}

	@Override
	public boolean setValue(String mod, String value) {
		this.value = "%"+value+"%";
		return true;
	}

	@Override
	public boolean appliesTo(String mod, boolean archived) {
		if(mod.equals("staff-recoveries"))
			return !archived;
		return isMod(mod, "staff-punishments", "staff-recoveries");
	}

}
