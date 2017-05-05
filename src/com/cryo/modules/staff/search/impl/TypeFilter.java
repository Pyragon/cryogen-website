package com.cryo.modules.staff.search.impl;

import com.cryo.modules.staff.search.Filter;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 03, 2017 at 9:37:26 AM
 */
public class TypeFilter extends Filter {
	
	public TypeFilter() {
		super("type");
	}

	@Override
	public String getFilter() {
		return "type=?";
	}

	@Override
	public boolean setValue(String value) {
		value = value.toLowerCase();
		if(!value.equals("mute") && !value.equals("ban"))
			return false;
		this.value = value.equals("mute") ? 0 : 1;
		return true;
	}
	
}
