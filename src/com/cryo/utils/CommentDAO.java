package com.cryo.utils;

import java.sql.Timestamp;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 06, 2017 at 11:57:39 PM
 */
@RequiredArgsConstructor
@Data
public class CommentDAO {
	
	private final int id, report_id, report_type;
	private final String username, comment;
	private final Timestamp time;
	
}
