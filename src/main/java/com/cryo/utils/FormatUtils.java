package com.cryo.utils;

import com.cryo.entities.accounts.Account;
import com.cryo.modules.accounts.AccountUtils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FormatUtils {

	public static String formatUser(String name) {
		Account account = AccountUtils.getAccount(name);
		name = Utilities.formatNameForDisplay(name);
		if(account != null)
			name = AccountUtils.crownHTML(account);
		return name;
	}

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

	public String formatNumber(double num) {
		return formatNumber(num, "###,###,###");
	}

	public String formatNumber(double num, String format) {
		return new DecimalFormat(format).format(num);
	}
	
}
