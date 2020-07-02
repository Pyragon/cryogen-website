package com.cryo.entities;

import java.util.HashMap;

import com.cryo.utils.Utilities;

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
	private final HashMap<Integer, Integer> reqs;
	
	public String getReqString() {
		String string = "";
		boolean data = false;
		for(Integer skill_id : reqs.keySet()) {
			Integer req = reqs.get(skill_id);
			if(data)
				string += ", ";
			string += req+" "+Utilities.SKILL_NAME[skill_id];
			data = true;
		}
		return string;
	}
	
}
