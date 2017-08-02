package com.cryo.db.impl;

import java.util.ArrayList;
import java.util.Arrays;

import com.cryo.Website;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.utils.BCrypt;
import com.google.gson.Gson;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: July 02, 2017 at 3:52:07 AM
 */
public class PreviousConnection extends DatabaseConnection {
	
	public PreviousConnection() {
		super("cryogen_previous");
	}
	
	@SuppressWarnings("unchecked")
	private final SQLQuery GET_HASHES = (set) -> {
		if(empty(set)) return null;
		String hashes = getString(set, "hashes");
		if(hashes.equals(""))
			return null;
		return new Object[] { (ArrayList<String>) new Gson().fromJson(hashes, ArrayList.class) };
	};
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getHashes(String salt) {
		Object[] data = select("passwords", "salt=?", GET_HASHES, salt);
		return data == null ? null : (ArrayList<String>) data[0];
	}
	
	public static PreviousConnection connection() {
		return (PreviousConnection) Website.instance().getConnectionManager().getConnection(Connection.PREVIOUS);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch(opcode) {
			case "add-prev-hash":
				String salt = (String) data[1];
				String new_hash = (String) data[2]; 
				ArrayList<String> hashes = getHashes(salt);
				if(hashes == null) {
					hashes = new ArrayList<String>();
					hashes.add(new_hash);
					String json = new Gson().toJson(hashes);
					insert("passwords", salt, json);
					break;
				}
				if(!hashes.contains(new_hash)) {
					hashes.add(new_hash);
				}
				String json = new Gson().toJson(hashes);
				set("passwords", "hashes=?", "salt=?", json, salt);
				break;
			case "compare-hashes":
				String[] compare = (String[]) data[1];
				String username = (String) data[2];
				data = GlobalConnection.connection().handleRequest("get-salt", username);
				if(data == null) return null;
				salt = (String) data[0];
				int[] results = new int[compare.length];
				hashes = getHashes(salt);
				if(hashes == null) {
					return null;
				}
				for(int i = 0; i < compare.length; i++) {
					String hash = compare[i];
					if(hash.equals("")) {
						results[i] = -1;
						continue;
					}
					hash = BCrypt.hashPassword(hash, salt);
					results[i] = hashes.contains(hash) ? 1 : 0;
					System.out.println("i: "+i+": "+hashes.contains(hash));
				}
				return new Object[] { results };
		}
		return null;
	}
	
}
