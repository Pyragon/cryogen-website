package com.cryo.modules.account.entities;

import java.sql.Timestamp;

import lombok.Getter;

public class PlayerReport extends Report {
	
	private @Getter String offender, rule, proof;

	public PlayerReport(int id, String username, String title, String offender, String rule, String info, String proof, String lastAction, int commentList, Timestamp time, Timestamp archived, boolean active) {
		super(id, username, title, info, lastAction, commentList, time, archived, active);
		this.offender = offender;
		this.rule = rule;
		this.proof = proof;
	}
	
	public Object[] getCreationData() {
		return new Object[] { "DEFAULT", username, title, offender, rule, info, proof, lastAction, commentList, "DEFAULT", "NULL", "DEFAULT", "DEFAULT" };
	}
	
	public int type() {
		return 1;
	}

}
