package com.cryo.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.cryo.Website;
import com.google.gson.Gson;

public class Tools {
	
	public String unescapeHtml(String html) {
		System.out.println(html+"testtt");
		return html;
	}
	
	public static void main(String[] args) {
		HashMap<Integer, Integer> cart = new HashMap<>();
		cart.put(1, 1);
		System.out.println(Website.buildGson().toJson(cart));
	}

}
