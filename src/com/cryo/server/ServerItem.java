package com.cryo.server;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 30, 2017 at 2:22:32 AM
 */
@Data
public class ServerItem {
	
	private final int id;
	private final String name;
	private final String description;
	private final int[] reqs;
	
}
