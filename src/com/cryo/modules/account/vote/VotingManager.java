package com.cryo.modules.account.vote;

import java.util.ArrayList;
import java.util.Random;

import com.cryo.db.impl.VotingConnection;

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
	
	@SuppressWarnings("unchecked")
	public ArrayList<AuthDAO> getAuthList() {
		Object[] data = VotingConnection.connection().handleRequest("get-auths", username);
		if(data == null)
			return new ArrayList<>();
		return (ArrayList<AuthDAO>) data[0];
	}
	
	public int getTime(int site) {
		if(new Random().nextInt(2) == 1)
			return 0;
		return 1;
	}
	
}
