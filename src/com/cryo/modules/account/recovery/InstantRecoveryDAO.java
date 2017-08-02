package com.cryo.modules.account.recovery;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: August 02, 2017 at 12:44:48 AM
 */
@RequiredArgsConstructor
@Data
public class InstantRecoveryDAO {
	
	private final String id, rand;
	
	private final int method, status;
	
}
