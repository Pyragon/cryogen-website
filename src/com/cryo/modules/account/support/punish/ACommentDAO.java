package com.cryo.modules.account.support.punish;

import java.sql.Timestamp;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 29, 2017 at 1:10:58 AM
 */
@RequiredArgsConstructor
@Data
public class ACommentDAO {
	
	private final int id, appeal_id;
	private final String username, comment;
	private final Timestamp time;
	

}
