package com.cryo.utils;

import com.cryo.entities.accounts.Account;
import com.cryo.modules.account.AccountUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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

	public String stampToTerm(Timestamp startStamp, Timestamp endStamp) {
		DateTime start = new DateTime(Math.min(startStamp.getTime(), endStamp.getTime()));
		DateTime end = new DateTime(Math.max(startStamp.getTime(), endStamp.getTime()));

		Period period = new Period(start, end);

		PeriodFormatter formatter = new PeriodFormatterBuilder()
				.appendYears().appendSuffix(" year", " years")
				.appendMonths().appendSuffix(" month", " months")
				.appendSeparator(" and ")
				.appendDays().appendSuffix(" day", " days")
				.printZeroNever()
				.toFormatter();

		return formatter.print(period);
	}

	public String formatTimestamp(Timestamp time) {
		return formatTimestamp(time, "MMMMM dd, YYYY");
	}
	
	public String formatTimestamp(Timestamp time, String format) {
		if(time == null) return "N/A";
		return format(time.getTime(), format);
	}

	public String formatTime(Timestamp time) {
		if(time == null) return "N/A";
		return format(time.getTime(), "MMMMM dd, YYYY @ hh:mm a");
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
