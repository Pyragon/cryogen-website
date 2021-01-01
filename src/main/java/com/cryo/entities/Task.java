package com.cryo.entities;

import com.cryo.Website;
import lombok.Data;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 13, 2017 at 2:59:27 PM
 */
@Data
public abstract class Task {
	
	private final String time;
	
	protected Website website;
	
	public abstract void run();
	
}
