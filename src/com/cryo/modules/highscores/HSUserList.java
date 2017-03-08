package com.cryo.modules.highscores;

import java.util.ArrayList;

import lombok.Getter;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:19:24 PM
 */
public class HSUserList {
	
	private @Getter ArrayList<HSUser> list;
	
	public HSUserList() {
		list = new ArrayList<>();
	}
	
	public void add(HSUser user) {
		list.add(user);
	}
	
}
