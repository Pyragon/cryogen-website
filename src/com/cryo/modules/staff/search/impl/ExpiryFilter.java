package com.cryo.modules.staff.search.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.cryo.modules.staff.search.Filter;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 04, 2017 at 6:13:34 AM
 */
public class ExpiryFilter extends Filter {
	
	private Timestamp from, to;

	public ExpiryFilter() {
		super("expiry");
	}

	@Override
	public String getFilter(String mod) {
		if(from == null || to == null)
			return "expiry IS NULL";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return "expiry >= '"+format.format(from)+"' AND expiry < '"+format.format(to)+"'";
	}

	@Override
	public boolean setValue(String mod, String value) {
		value = value.toLowerCase();
		if(value.equals("never")) {
			this.from = null;
			this.to = null;
			return true;
		}
		String[] values = value.split("-");
		if(values.length != 2)
			return false;
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			from = new Timestamp(format.parse(values[0]).getTime());
			to = new Timestamp(format.parse(values[1]).getTime());
			if(from.getTime() > to.getTime())
				return false;
			this.value = null;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean appliesTo(String mod) {
		return mod.equals("punish");
	}
	
}
