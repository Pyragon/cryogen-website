package com.cryo.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 8:23:49 PM
 */
public class DateUtils {
	
	//date1 = before, date2 = after, difference = days between 1 and 2
	public static long getDateDiff(Date date1, Date date2, TimeUnit unit) {
		long diff_in_millis = date2.getTime() - date1.getTime();
		return unit.convert(diff_in_millis, TimeUnit.MILLISECONDS);
	}
	
	public static Date getDate(String string, String format) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			Date date = formatter.parse(string);
			return date;
		} catch(Exception e) { }
		return null;
	}
	
	public static boolean isValidDate(String string, String format) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			Date date = formatter.parse(string);
			return date != null;
		} catch(Exception e) { }
		return false;
	}
	
}
