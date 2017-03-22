package com.cryo.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 8:23:49 PM
 */
public class DateFormatter {
	
	public String formatTimestamp(Timestamp time, String format) {
		return format(time.getTime(), format);
	}
	
	public String format(long dateline, String format) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(dateline);
		Date d = c.getTime();
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(d);
	}
	
}
