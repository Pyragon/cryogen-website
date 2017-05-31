package com.cryo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.cryo.Website;
import com.google.gson.Gson;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 30, 2017 at 2:23:56 AM
 */
public class ServerConnection {
	
	public static String SERVER_URL = "http://"+Website.getProperties().getProperty("server-ip");
	
	public static ServerItem getServerItem(String item) {
		item = StringEscapeUtils.escapeHtml4(item).replaceAll(" ", "%20");
		String url = SERVER_URL+"/grab_data?action=get-item&item="+item;
		String response = getResponse(url);
		if(response == null) return null;
		Properties prop = new Gson().fromJson(response, Properties.class);
		if(prop == null) return null;
		boolean success = Boolean.parseBoolean(prop.getProperty("success"));
		if(!success) return null;
		int id = Integer.parseInt(prop.getProperty("id"));
		String name = (String) prop.getProperty("name");
		String desc = (String) prop.getProperty("examine");
		HashMap<Integer, Integer> reqs = new HashMap<>();
		for(String s : prop.stringPropertyNames()) {
			if(s == null) continue;
			if(s.contains("skill_")) {
				String sVal = prop.getProperty(s);
				s = s.substring(6);
				if(!StringUtils.isNumeric(s) || !StringUtils.isNumeric(sVal))
					return null;
				int skill_id = Integer.parseInt(s);
				int level = Integer.parseInt(sVal);
				if(skill_id >= 0 && skill_id <= 25 && level > 0 && level < 99)
					reqs.put(skill_id, level);
			}
		}
		return new ServerItem(id, name, desc, reqs);
	}
	
	public static String getResponse(String url) {
		try {
			URL dao = new URL(url);
			HttpURLConnection con = (HttpURLConnection) dao.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			con.getResponseCode();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = reader.readLine()) != null)
				response.append(line);
			reader.close();
			return response.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "Error getting response.";
		}
	}
	
}
