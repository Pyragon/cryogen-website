package com.cryo.modules.search.impl;

import com.cryo.db.DatabaseConnection;
import com.cryo.modules.search.Filter;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 03, 2017 at 10:37:36 AM
 */
public class ExpiredFilter extends Filter {
	
	private boolean expired;
	
	public ExpiredFilter() {
		super("expired");
	}

	@Override
	public String getFilter(String mod) {
		return "expiry "+(expired ? "<" : ">")+" NOW()";
	}
	
	@Override
	public boolean setValue(String mod, String value) {
		value = value.toLowerCase();
		if(!value.equals("true") && !value.equals("false")
				&& !value.equals("yes") && !value.equals("no"))
			return false;
		expired = (value.equals("true") || value.equals("yes"));
		this.value = null;
		return true;
	}

	@Override
	public boolean appliesTo(String mod) {
		return isMod(mod, "punish");
	}
	
}
