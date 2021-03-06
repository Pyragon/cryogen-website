package com.cryo.modules.search.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cryo.modules.search.Filter;
import com.cryo.utils.DateSpan;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 23, 2017 at 7:03:42 PM
 */
public class DateFilter extends Filter {
	
	private DateSpan span;
	
	public DateFilter() {
		super("date");
	}

	@Override
	public String getFilter(String mod) {
		if(span == null)
			return null;
		return "date BETWEEN "+span.format("from")+" AND "+span.format("to");
	}
	
	@Override
	public boolean setValue(String mod, String value) {
		value = value.toLowerCase();
		String[] values = value.split("-");
		if(values.length != 2)
			return false;
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			span = new DateSpan(new Timestamp(format.parse(values[0]).getTime()), new Timestamp(format.parse(values[1]).getTime()));
			if(span.invalid())
				return false;
			this.value = null;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean appliesTo(String mod, boolean archive) {
		return isMod(mod, "punishments", "reports", "announcements", "staff-punishments", "staff-reports", "staff-appeals", "staff-recoveries");
	}
	
}
