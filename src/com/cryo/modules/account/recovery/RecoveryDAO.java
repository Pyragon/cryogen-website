package com.cryo.modules.account.recovery;

import java.sql.Timestamp;
import java.util.Date;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: July 25, 2017 at 3:38:31 AM
 */
@Data
@RequiredArgsConstructor
public class RecoveryDAO {
	
	private final String id, username, email, forumId;
	
	private final long creation;
	
	private final String cico, additional;
	
	private final int[] passes;
	
	private final int status;
	
	private final String newPass, reason;
	
	public Object[] data() {
		return new Object[] { id, username, email, forumId, creation == 0L ? "NULL" : new Timestamp(creation), cico, additional, passes[0], passes[1], passes[2], status, newPass, reason };
	}
	
	public int getPass(int index) {
		return passes[index];
	}
	
	public int getDaysOff() {
		if(creation == 0L) return -1;
		return 12;
	}
	
}
