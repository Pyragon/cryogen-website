package com.cryo.utils;

import java.text.DecimalFormat;

import spark.Request;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:48:36 PM
 */
public class Utilities {
	
	public static String formatNumer(long number) {
		DecimalFormat f = new DecimalFormat("###,###,###");
		return f.format(number);
	}
	
	public static String formatMessage(String message) {
		if(message.contains("{{username}}")) {
			String name = "Guest";
			//TODO - check for logged in user
			message = message.replace("{{username}}", name);
		}
		message = message.replace("{{/username}}", "{{username}}");
		return message;
	}
	
	public static String getRequestUsername(Request request) {
		return request.queryParams("username");
	}
	
	public static boolean isMiniLogin(Request request) {
		return request.queryParams("mini-login").equals("true");
	}
	
	public static String getRequestPassword(Request request) {
		return request.queryParams("password");
	}
	
	public static String getRequestRedirect(Request request) {
		return request.queryParams("redirect");
	}
	
}
