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
	private final int active;
	
}
