package com.cryo.modules.account.support.punish;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 22, 2017 at 8:41:12 AM
 */
@RequiredArgsConstructor
@Data
public class AppealDAO {
	
	private final int id;
	private final String title, message;
	private String reason;
	private final int active;
	
	public String getDeclineReason() {
		return reason;
	}
	
	public String getStatus() {
		switch(active) {
			case 0: return "Pending";
			case 1: return "Accepted";
			case 2: return "Declined";
			default: return "Error: contact Admin";
		}
	}
	
	public String getColour() {
		switch(active) {
			case 0: return "";
			case 1: return "color-green";
			case 2: return "color-red";
			default: return "color-red";
		}
	}
	
}
