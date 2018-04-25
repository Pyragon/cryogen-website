package com.cryo.modules.highscores;

import java.util.ArrayList;

import com.cryo.modules.highscores.HSUtils.HSData;

import lombok.Getter;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:19:24 PM
 */
public class HSDataList {
	
	private @Getter ArrayList<HSData> list;
	
	public HSDataList() {
		list = new ArrayList<>();
	}
	
	public void add(HSData user) {
		list.add(user);
	}
	
}
