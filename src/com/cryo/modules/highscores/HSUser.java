package com.cryo.modules.highscores;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 7, 2017 at 9:17:59 PM
 */
@RequiredArgsConstructor
public class HSUser {
	
	private final @Getter int rank;
	
	private final @Getter String name;
	
	private final @Getter String totalLevel, totalXP;
	
	private @Getter @Setter double[] xp;
	
}
