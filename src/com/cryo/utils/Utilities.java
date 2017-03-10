package com.cryo.utils;

import java.text.DecimalFormat;
import java.util.Properties;

import com.google.gson.Gson;

import spark.Request;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:48:36 PM
 */
public class Utilities {
	
	private static Utilities INSTANCE;
	
	public static Utilities instance() {
		if(INSTANCE == null)
			INSTANCE = new Utilities();
		return INSTANCE;
	}
	
	public String test() {
		return "test";
	}
	
	public String renderLink(String href, String text) {
		return "<a href='"+href+"'>"+text+"</a>";
	}
	
	public static String json(Properties prop) {
		return new Gson().toJson(prop);
	}
	
	public static boolean hasNullOrEmpty(String...strings) {
		for(String s : strings)
			if(s == null || s.equals(""))
				return true;
		return false;
	}
	
	public String formatDouble(double number) {
		DecimalFormat f = new DecimalFormat("###,###,###");
		return f.format(number);
	}
	
	public String formatLong(long number) {
		DecimalFormat f = new DecimalFormat("###,###,###");
		return f.format(number);
	}
	
	public static String formatName(String name) {
		if (name == null)
			return "";
		name = name.replaceAll("_", " ");
		name = name.toLowerCase();
		StringBuilder newName = new StringBuilder();
		boolean wasSpace = true;
		for (int i = 0; i < name.length(); i++) {
			if (wasSpace) {
				newName.append(("" + name.charAt(i)).toUpperCase());
				wasSpace = false;
			} else {
				newName.append(name.charAt(i));
			}
			if (name.charAt(i) == ' ') {
				wasSpace = true;
			}
		}
		return newName.toString();
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
