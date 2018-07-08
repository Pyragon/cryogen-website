package com.cryo.db.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
 *         Created on: July 02, 2017 at 3:52:07 AM
 */
public class PreviousConnection extends DatabaseConnection {

	public PreviousConnection() {
		super("cryogen_previous");
	}

	@SuppressWarnings("unchecked")
	private final SQLQuery GET_HASHES = (set) -> {
		if (empty(set))
			return null;
		String hashes = getString(set, "hashes");
		if (hashes.equals(""))
			return null;
		return new Object[] { (ArrayList<String>) new Gson().fromJson(hashes, ArrayList.class) };
	};

	private final SQLQuery GET_IP_DATA = (set) -> {
		if (empty(set))
			return null;
		HashMap<String, PreviousIP> previous = new HashMap<>();
		String username = getString(set, "username");
		String value = getString(set, "value");
		String[] values = value.split(", ");
		int index = 0;
		while (index < values.length) {
			String ip = values[index++];
			long time = Long.parseLong(values[index++]);
			previous.put(ip, new PreviousIP(username, ip, new Timestamp(time)));
		}
		return new Object[] { previous };
	};

	@SuppressWarnings("unchecked")
	public ArrayList<String> getHashes(String salt) {
		Object[] data = select("passwords", "salt=?", GET_HASHES, salt);
		return data == null ? null : (ArrayList<String>) data[0];
	}

	public String toString(HashMap<String, PreviousIP> list) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<PreviousIP> it = list	.values()
											.iterator(); it.hasNext();) {
			PreviousIP previous = it.next();
			builder.append(previous.getIp());
			builder.append(", ");
			builder.append(previous	.getDate()
									.getTime());
			if (it.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}

	public static PreviousConnection connection() {
		return (PreviousConnection) Website	.instance()
											.getConnectionManager()
											.getConnection(Connection.PREVIOUS);
	}

	@Override
	public Object[] handleRequest(Object... data) {
		String opcode = (String) data[0];
		switch (opcode) {
		case "add-new-ip":
			String username = (String) data[1];
			String ip = (String) data[2];
			PreviousIP previous = new PreviousIP(username, ip, new Timestamp(new Date().getTime()));
			data = handleRequest("get-ip-data", username);
			if (data == null) {
				HashMap<String, PreviousIP> list = new HashMap<>();
				list.put(ip, previous);
				insert("ips", username, toString(list));
				return null;
			}
			HashMap<String, PreviousIP> list = (HashMap<String, PreviousIP>) data[0];
			if (list == null)
				return null;
			if (list.containsKey(ip))
				return null;
			list.put(ip, previous);
			set("ips", "value=?", "username=?", toString(list), username);
			break;
		case "get-ip-data":
			username = (String) data[1];
			return select("ips", "username=?", GET_IP_DATA, username);
		case "add-prev-hash":
			String salt = (String) data[1];
			String new_hash = (String) data[2];
			ArrayList<String> hashes = getHashes(salt);
			if (hashes == null) {
				hashes = new ArrayList<String>();
				hashes.add(new_hash);
				String json = new Gson().toJson(hashes);
				insert("passwords", salt, json);
				break;
			}
			if (!hashes.contains(new_hash)) {
				hashes.add(new_hash);
			}
			String json = new Gson().toJson(hashes);
			set("passwords", "hashes=?", "salt=?", json, salt);
			break;
		case "compare-hashes":
			String[] compare = (String[]) data[1];
			username = (String) data[2];
			data = GlobalConnection	.connection()
									.handleRequest("get-salt", username);
			if (data == null)
				return null;
			salt = (String) data[0];
			int[] results = new int[compare.length];
			hashes = getHashes(salt);
			if (hashes == null)
				return null;
			for (int i = 0; i < compare.length; i++) {
				String hash = compare[i];
				if (hash.equals("")) {
					results[i] = -1;
					continue;
				}
				hash = BCrypt.hashPassword(hash, salt);
				results[i] = hashes.contains(hash) ? 1 : 0;
			}
			return new Object[] { results };
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, PreviousIP> getIPData(String username) {
		Object[] data = connection().handleRequest("get-ip-data", username);
		if (data == null)
			return null;
		return (HashMap<String, PreviousIP>) data[0];
	}

	public static List<PreviousIP> getSorted(String username) {
		HashMap<String, PreviousIP> list = getIPData(username);
		if (list == null)
			return null;
		List<PreviousIP> previous = new ArrayList<PreviousIP>(list.values());
		previous.sort((v1, v2) -> {
			return (int) (v1.getDate()
							.getTime() - v2	.getDate()
											.getTime());
		});
		return previous;
	}

	public static PreviousIP getLatestIP(String username) {
		List<PreviousIP> previous = getSorted(username);
		return previous.get(0);
	}

	@Data
	@RequiredArgsConstructor
	public static class PreviousIP {

		private final String username, ip;
		private final Timestamp date;

	}

}
