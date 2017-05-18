package com.cryo.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cryo.db.impl.GlobalConnection;
import com.cryo.modules.account.AccountDAO;

import lombok.*;
import spark.Request;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 14, 2017 at 1:58:17 AM
 */
public class CookieManager {
	
	//User loads page, if sess_id is present, find acc from id
	//sess_id = username+password+salt
	
	public static boolean isLoggedIn(Request request) {
		if(request.cookies().containsKey("cryo-sess")) {
			String sess_id = request.cookie("cryo-sess");
			Object[] data = GlobalConnection.connection().handleRequest("get-acc-from-sess", sess_id);
			if(data == null)
				return false;
			return (AccountDAO) data[0] != null;
		}
		return false;
	}
	
	public static String getUsername(Request request) {
		AccountDAO account = getAccount(request);
		if(account == null)
			return "";
		return account.getUsername();
	}
	
	public static AccountDAO getAccount(Request request) {
		if(request.cookies().containsKey("cryo-sess")) {
			String sess_id = request.cookie("cryo-sess");
			Object[] data = GlobalConnection.connection().handleRequest("get-acc-from-sess", sess_id);
			if(data == null)
				return null;
			return (AccountDAO) data[0];
		}
		return null;
	}
	
	public static String hashSessId(String sess_id) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(sess_id.getBytes("UTF-8"));
			return String.format("%064x", new BigInteger(1, md.digest()));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "invalid_sess_id";
	}
	
	public static String generateSessId(String username, String hash, String salt) {
		String toHash = username;
		toHash += hash;
		toHash += salt;
		return hashSessId(toHash);
	}
	
}
