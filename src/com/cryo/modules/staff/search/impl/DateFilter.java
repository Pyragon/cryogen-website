package com.cryo.modules.staff.search.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cryo.modules.staff.search.Filter;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 23, 2017 at 7:03:42 PM
 */
public class DateFilter extends Filter {
	
	private Date from;
	private Date to;
	
	public DateFilter() {
		super("date");
	}

	@Override
	public String getFilter(String mod) {
		if(from == null || to == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return "time >= '"+format.format(from)+"' AND time < '"+format.format(to)+"'";
	}
	
	@Override
	public boolean setValue(String mod, String value) {
		value = value.toLowerCase();
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
		return mod.equals("appeal");
	}
	
}
