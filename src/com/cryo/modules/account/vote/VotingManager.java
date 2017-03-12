package com.cryo.modules.account.vote;

import java.util.Random;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 11, 2017 at 2:34:29 PM
 */
@RequiredArgsConstructor
public class VotingManager {
	
	private final @Getter String username;
	
	public int getTime(int site) {
		if(new Random().nextInt(2) == 1)
			return 0;
		return 1;
	}
	
}
