package com.cryo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 23, 2017 at 9:09:41 PM
 */
@Data
public class DateSpan {
	
	public DateSpan(Date from, Date to) {
		this.from = from;
		Calendar c = Calendar.getInstance();
		c.setTime(to);
		c.add(Calendar.DAY_OF_MONTH, 1);
		this.to = new Date(c.getTimeInMillis());
	}
	
	private final Date from;
	private final Date to;
	
	public boolean invalid() {
		return from.getTime() > to.getTime();
	}
	
	
	public String format(String which) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return "'"+format.format(which.equals("from") ? from : to)+"'";
	}
	
}
