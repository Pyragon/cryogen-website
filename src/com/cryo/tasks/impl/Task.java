package com.cryo.tasks.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 13, 2017 at 2:59:27 PM
 */
@RequiredArgsConstructor
public abstract class Task {
	
	private final @Getter int hour, minute, second;
	
	public abstract void run();
	
}
