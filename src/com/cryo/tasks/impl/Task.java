package com.cryo.tasks.impl;

import com.cryo.Website;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 13, 2017 at 2:59:27 PM
 */
@RequiredArgsConstructor
public abstract class Task {
	
	private final @Getter int hour, minute, second;
	
	protected @Setter Website website;
	
	public abstract void run();
	
}
