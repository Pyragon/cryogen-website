package com.cryo.modules.forums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 8, 2017 at 5:40:34 AM
 */
@RequiredArgsConstructor
public class ForumUser {
	
	private final @Getter int uID;
	private final @Getter String username;
	private final @Getter int usergroup;
	private final @Getter int displaygroup;
	
}
