package com.cryo.tasks.impl;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 13, 2017 at 3:05:39 PM
 */
public class EmailVerifyTask extends Task {
	
	public EmailVerifyTask() {
		super(-1, -1, 0); //-1 means will happen always. so will happen every hour, every minute, only 1 every 60 seconds
	}

	@Override
	public void run() {
		System.out.println("running");
	}
	
}
