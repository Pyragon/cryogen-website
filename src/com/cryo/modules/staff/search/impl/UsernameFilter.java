package com.cryo.modules.staff.search.impl;

import com.cryo.modules.staff.search.Filter;

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
	public String getFilter() {
		return "username LIKE ?";
	}

	@Override
	public boolean setValue(String value) {
		this.value = value;
		return true;
	}
	
}