package com.cryo.managers.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.apache.http.client.utils.URIBuilder;

import com.cryo.Website;
import com.cryo.entities.ServerItem;
import com.cryo.utils.Logger;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 30, 2017 at 2:23:56 AM
 */
public class ServerConnection {
	
	private @Getter String endpoint;
	
	private @Getter Properties params;
	
	private boolean fetching;
	
	private boolean fetched;
	
	private @Getter Properties response;
	
	public static String authToken;
	
	public ServerConnection(String endpoint, Properties params) {
		this.endpoint = endpoint;
		this.params = params;
		if(authToken == null)
			fetchNewAuthToken();
	}
	
	public void fetchData() {
		fetching = true;
		String response = getResponse(endpoint, params);
		fetching = false;
		if(response == null) {
			this.response = null;
			return;
		}
		this.response = Website.buildGson().fromJson(response, Properties.class);
		if(failed() && this.response.getProperty("error").contains("Invalid login") && !fetched)
			fetchNewAuthToken();
		fetched = true;
	}
	
	public void fetchNewAuthToken() {
		Properties prop = new Properties();
		prop.put("username", Website.getProperties().getProperty("cryobot-user"));
		prop.put("password", Website.getProperties().getProperty("cryobot-pass"));
		String response = getResponse("/login", prop);
		if(response == null) {
			Logger.log(this.getClass(), "Getting auth token has failed!");
			return;
		}
		Properties res = Website.buildGson().fromJson(response, Properties.class);
		if(!res.containsKey("success"))
			return;
		boolean success = Boolean.parseBoolean(res.getProperty("success"));
		if(success)
			authToken = res.getProperty("token");
	}
	
	public boolean success() {
		if(response != null) {
			if(response.containsKey("success")) {
				return Boolean.parseBoolean(response.getProperty("success"));
			}
		}
		System.out.println("nah");
		return fetching == false && fetched == true && response != null;
	}
	
	public boolean failed() {
		if(response != null) {
			if(response.containsKey("success")) {
				return !Boolean.parseBoolean(response.getProperty("success"));
			}
		}
		return fetching == true || fetched == false || response == null;
	}
	
	public String getError() {
		if(!failed())
			return "Incorrect failure handled";
		if(response == null)
			return "Unable to parse response from server.";
		if(fetching == true || fetched == false)
			return "Still fetching from server.";
		if(!response.containsKey("success") || !response.containsKey("error"))
			return "JSON failure.";
		return response.getProperty("error");
	}
	
	public static ServerConnection getConnection(String endpoint, Object... values) {
		Properties prop = new Properties();
		int index = 0;
		while(index < values.length) {
			Object keyObj = values[index++];
			Object value = values[index++];
			if(keyObj instanceof Integer)
				prop.put((Integer) keyObj, value);
			else
				prop.put((String) keyObj, value);
		}
		ServerConnection con = new ServerConnection(endpoint, prop);
		con.fetchData();
		while(con.fetching)
			continue;
		return con;
	}
	
	public static String SERVER_URL = "http://"+Website.getProperties().getProperty("server-ip");
	
	public static ServerItem getServerItem(int id) {
		Properties prop = new Properties();
		prop.put("info", "get-item");
		prop.put("id", id);
		ServerConnection connection = new ServerConnection("/game", prop);
		connection.fetchData();
		if(connection.failed()) return null;
		Properties response = (Properties) connection.getResponse().get("info");
		String name = response.getProperty("name");
		String examine = response.getProperty("examine");
		Properties requirements = (Properties) response.get("requirements");
		HashMap<Integer, Integer> map = new HashMap<>();
		for(Object skill : requirements.keySet()) {
			Object requirement = requirements.get(skill);
			map.put((Integer) skill, (Integer) requirement);
		}
		return new ServerItem(id, name, examine, map);
	}
	
	public static String getResponse(String url, Properties prop) {
		try {
			URIBuilder b = new URIBuilder(SERVER_URL+""+url);
			for(Object key : prop.keySet()) {
				Object value = prop.get(key);
				b.addParameter(key.toString(), value.toString());
			}
			if(authToken != null)
				b.addParameter("token", authToken);
			URL dao = b.build().toURL();
			HttpURLConnection con = (HttpURLConnection) dao.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setReadTimeout(5000);
			con.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = reader.readLine()) != null)
				response.append(line);
			reader.close();
			return response.toString();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			return "Error getting response.";
		}
	}
	
}
