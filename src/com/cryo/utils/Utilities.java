package com.cryo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cryo.db.DBConnectionManager;
import com.cryo.db.impl.GlobalConnection;
import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;

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
		
	}
	
	public static boolean isNullOrEmpty(String... values) {
		return Arrays.stream(values).anyMatch(v -> StringUtils.isNullOrEmpty(v));
	}
	
	public static long roundUp(long num, long divisor) {
		return (num + divisor - 1) / divisor;
	}
	
	public int parseInt(String str) {
		return Integer.parseInt(str);
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

	public static final char[] VALID_CHARS = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', ' ' };
	
	public static final String[] INVALID_WORDS = { "admin", "mod" };
	
	public static String isValidDisplay(String name) {
		if(name.length() < 3 || name.length() > 12)
			return "Name must be between 3 and 12 characters.";
		if(containsInvalidCharacter(name))
			return "Name contains invalid character.";
		if(containsTwo(name))
			return "Name contains two spaces or hyphens in a row.";
		if(startsOrEndsWith(name))
			return "Name cannot start or end with a space or hyphen.";
		if(containsInvalidWords(name))
			return "Name contains invalid words.";
		return null;
	}
	
	public static boolean containsInvalidWords(String name) {
		return Arrays.stream(INVALID_WORDS).anyMatch(s -> name.toLowerCase().contains(s));
	}
	
	public static boolean startsOrEndsWith(String name) {
		char[] chars = { ' ', '-', '_' };
		for(char c : chars) {
			if(name.charAt(0) == c || name.charAt(name.length()-1) == c)
				return true;
		}
		return false;
	}
	
	public static boolean containsTwo(String name) {
		char last = 'a';
		for(int i = 0; i < name.length(); i++) {
			char cur = name.charAt(i);
			if(cur == ' ' || cur == '-' || cur == '_') {
				if(last == ' ' || last == '-' || last == '_')
					return true;
			}
			last = cur;
		}
		return false;
	}

	public static boolean containsInvalidCharacter(char c) {
		for (char vc : VALID_CHARS) {
			if (vc == c)
				return false;
		}
		return true;
	}

	public static boolean containsInvalidCharacter(String name) {
		for (char c : name.toCharArray()) {
			if (containsInvalidCharacter(c)) {
				System.out.println("Invalid: "+c);
				return true;
			}
		}
		return false;
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
			char c = name.charAt(i);
			newName.append(wasSpace ? Character.toString(c).toUpperCase() : c);
			if(wasSpace) wasSpace = false;
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
	
	public static String[] getWebsite(String[] url) {
		try {
			URI uri = new URI(url[0], url[1], url[2], url[3], null);
			URL ur = uri.toURL();
			BufferedReader reader = new BufferedReader(new InputStreamReader(ur.openStream()));
			ArrayList<String> lines = new ArrayList<String>();
			String line;
			while ((line = reader.readLine()) != null)
				lines.add(line);
			return lines.toArray(new String[lines.size()]);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String[] getWebsite(String url) {
		try {
			URI uri = new URI(url);
			URL ur = uri.toURL();
			BufferedReader reader = new BufferedReader(new InputStreamReader(ur.openStream()));
			ArrayList<String> lines = new ArrayList<String>();
			String line;
			while ((line = reader.readLine()) != null)
				lines.add(line);
			return lines.toArray(new String[lines.size()]);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
