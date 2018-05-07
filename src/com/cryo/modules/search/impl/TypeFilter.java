package com.cryo.modules.search.impl;

import org.apache.commons.lang3.StringUtils;

import com.cryo.modules.search.Filter;

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
	public String getFilter(String mod) {
		return "type=?";
	}

	@Override
	public boolean setValue(String mod, String value) {
		value = value.toLowerCase();
		if(!StringUtils.isNumeric(value))
			return false;
		int intVal = Integer.parseInt(value);
		if(intVal != 0 && intVal != 1)
			return false;
		this.value = intVal;
		return true;
	}

	@Override
	public boolean appliesTo(String mod) {
		return mod.equals("reports");
	}
	
}
