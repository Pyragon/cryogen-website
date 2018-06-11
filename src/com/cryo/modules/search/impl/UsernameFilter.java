package com.cryo.modules.search.impl;

import com.cryo.modules.search.Filter;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 04, 2017 at 1:52:47 AM
 */
public class UsernameFilter extends Filter {

	public UsernameFilter() {
		super("username");
	}

	@Override
	public String getFilter(String mod) {
		return "username LIKE ?";
	}

	@Override
	public boolean setValue(String mod, String value) {
		this.value = "%"+value+"%";
		return true;
	}

	@Override
	public boolean appliesTo(String mod) {
		return isMod(mod, "staff-reports", "staff-punishments");
	}
	
}
