package com.cryo.modules.account.vote;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import com.cryo.db.impl.VotingConnection;
import com.google.gson.Gson;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 11, 2017 at 2:34:29 PM
 */
@RequiredArgsConstructor
public class VotingManager {
	
	private final @Getter String username;
	
	public String getName(int index, boolean db) {
		switch(index) {
			case 0: return db ? "rune-server" : "Rune-Server";
			case 1: return db ? "runelocus" : "Runelocus";
			case 2: return db ? "toplist" : "MMORPG Toplist";
		default: return "Error loading site";	
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<AuthDAO> getAuthList() {
		Object[] data = VotingConnection.connection().handleRequest("get-auths", username);
		if(data == null)
			return new ArrayList<>();
		return (ArrayList<AuthDAO>) data[0];
	}
	
	public int getTime(int site) {
		Object[] data = VotingConnection.connection().handleRequest("get-time", username, getName(site, true));
		if(data == null)
			return 0;
		Timestamp timestamp = (Timestamp) data[0];
		long millis = timestamp.getTime();
		if(millis < System.currentTimeMillis())
			return 0;
		long diff = System.currentTimeMillis() - millis;
		long diffMinutes = diff / (60 * 1000);
		return (int) diffMinutes;
	}
	
}
