package com.cryo.server;

import java.util.Arrays;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 30, 2017 at 2:23:56 AM
 */
public class ServerConnection {
	
	public static ServerItem getServerItem(String item) {
		int[] reqs = new int[25];
		Arrays.fill(reqs, 1);
		return new ServerItem(1, "Test item", "This is our new test item", reqs);
	}
	
}
