package com.cryo.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import com.cryo.db.impl.VotingConnection;
import com.cryo.modules.account.entities.Auth;
import com.google.gson.Gson;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 11, 2017 at 2:34:29 PM
 */
public class VotingManager {
	
	
	public static String getName(int index, boolean db) {
		switch(index) {
			case 0: return db ? "rune-server" : "Rune-Server";
			case 1: return db ? "runelocus" : "Runelocus";
			case 2: return db ? "toplist" : "MMORPG Toplist";
		default: return "Error loading site";	
		}
	}
	
	public static String buildURL(String username, int site) {
		return "http://google.ca";
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Auth> getAuthList(String username) {
		Object[] data = VotingConnection.connection().handleRequest("get-auths", username);
		if(data == null)
			return new ArrayList<>();
		return (ArrayList<Auth>) data[0];
	}
	
	public static int getTime(String username, int site) {
		Object[] data = VotingConnection.connection().handleRequest("get-time", username, getName(site, true));
		if(data == null)
			return 0;
		Timestamp timestamp = (Timestamp) data[0];
		long millis = timestamp.getTime();
		if(millis < System.currentTimeMillis())
			return 0;
		long diff = System.currentTimeMillis() - millis;
		long seconds = diff / (1000);
		return (int) seconds;
	}
	
}
