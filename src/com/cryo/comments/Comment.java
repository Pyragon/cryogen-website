package com.cryo.comments;

import java.sql.Timestamp;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 06, 2017 at 11:57:39 PM
 */
@RequiredArgsConstructor
@Data
public class Comment {
	
	private final int id, listId;
	private final String username, comment;
	private final Timestamp time;
	
	public Object[] getData() {
		return new Object[] { "DEFAULT", listId, username, comment, "DEFAULT" };
	}
	
}
