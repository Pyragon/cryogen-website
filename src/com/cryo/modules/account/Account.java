package com.cryo.modules.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:55:54 PM
 */
@RequiredArgsConstructor
public class Account {
	
	private final @Getter String username;
	
	private final @Getter int rights, donator;
	
}
