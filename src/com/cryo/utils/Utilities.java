package com.cryo.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cryo.db.DBConnectionManager;
import com.cryo.db.impl.GlobalConnection;
import com.google.gson.Gson;

import spark.Request;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: Mar 7, 2017 at 9:48:36 PM
 */
public class Utilities {
	
	private static Utilities INSTANCE;
	
	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore",
			"Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction", "Summoning", "Dungeoneering" };
	
	public static Utilities instance() {
		if (INSTANCE == null)
			INSTANCE = new Utilities();
		return INSTANCE;
	}
	
	public static void main(String[] args) {
		String hash = CookieManager.generateSessId("brandon", "$2a$10$y4uaUQSW/ebu.ZP/TOGelObG7Ainrbpu6klJ30aE5KeL7ILBCq/9O", "$2a$10$y4uaUQSW/ebu.ZP/TOGelO");
		System.out.println(hash);
	}
	
	public static long roundUp(long num, long divisor) {
		return (num + divisor - 1) / divisor;
	}
	
	public static boolean matches(String name) {
		CharSequence seq = name;
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_ -]*", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(seq);
		return matcher.matches();
	}
	
	public static int getOnlinePlayers() {
		Object[] data = GlobalConnection.connection().handleRequest("get-misc-data", "players_logged");
		if (data == null)
			return -1;
		return Integer.parseInt((String) data[0]);
	}
	
	public String isValidDisplay(String name) {
		if (!name.matches("[a-zA-Z0-9_ -]*"))
			return "Display name contains invalid characters";
		if (name.length() < 3 || name.length() > 12)
			return "Display name must be between 3 and 12 characters";
		if (name.replace(" ", "_").matches("\\w*(-{2}|_{2}|-_|_-)\\w*"))
			return "Display name cannot contain two spaces, underscores, or hyphens in a row";
		if (name.startsWith("-") || name.endsWith("-"))
			return "Display name cannot start or end with a hyphen";
		if (name.startsWith("_") || name.endsWith("_"))
			return "Display name cannot start or end with an underscore";
		if (name.startsWith(" ") || name.endsWith(" "))
			return "Display name cannot start or end with a space";
		if (name.toLowerCase().contains("mod") || name.toLowerCase().contains("admin"))
			return "Display name contains invalid words";
		return "";
	}
	
	public static String formatNameForProtocol(String name) {
		if (name == null)
			return "";
		name = name.replaceAll(" ", "_");
		name = name.toLowerCase();
		return name;
	}
	
	public static String formatNameForDisplay(String name) {
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
	
	public String renderLink(String href, String text) {
		return "<a href='" + href + "'>" + text + "</a>";
	}
	
	public static String json(Properties prop) {
		return new Gson().toJson(prop);
	}
	
	public static boolean hasNullOrEmpty(String... strings) {
		for (String s : strings)
			if (s == null || s.equals(""))
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
	
	@SuppressWarnings({ "rawtypes" })
	public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile().replaceAll("%20", " ")));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}
	
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				try {
					classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
				} catch (Throwable e) {
					
				}
			}
		}
		return classes;
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
		if (message.contains("{{username}}")) {
			String name = "Guest";
			// TODO - check for logged in user
			message = message.replace("{{username}}", name);
		}
		message = message.replace("{{/username}}", "{{username}}");
		return message;
	}
	
}
