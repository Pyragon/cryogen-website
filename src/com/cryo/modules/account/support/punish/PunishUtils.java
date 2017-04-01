package com.cryo.modules.account.support.punish;

import java.util.ArrayList;

import com.cryo.db.impl.PunishmentConnection;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 22, 2017 at 2:34:49 AM
 */
public class PunishUtils {
	
	@SuppressWarnings("unchecked")
	public ArrayList<PunishDAO> getPunishments(String username) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-punishments", username);
		if(data == null)
			return new ArrayList<PunishDAO>();
		return (ArrayList<PunishDAO>) data[0];
	}
	
	public PunishDAO getPunishmentFromAppeal(int appealId) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-punishment-from-appeal", appealId);
		if(data == null)
			return null;
		return (PunishDAO) data[0];
	}
	
	public AppealDAO getAppealFromPunishment(int punishment) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-punishment", punishment);
		if(data == null)
			return null;
		PunishDAO punish = (PunishDAO) data[0];
		int appealId = punish.getAppealId();
		data = PunishmentConnection.connection().handleRequest("get-appeal", appealId);
		if(data == null)
			return null;
		return (AppealDAO) data[0];
	}
	
	public AppealDAO getAppeal(int appealId) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-appeal", appealId);
		if(data == null)
			return null;
		return (AppealDAO) data[0];
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ACommentDAO> getComments(int appealId) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-comments", appealId);
		if(data == null)
			return null;
		return (ArrayList<ACommentDAO>) data[0];
	}
	
	public static void createAppeal(int punishId, String title, String detailed) {
		PunishmentConnection.connection().handleRequest("create-appeal", title, detailed, punishId);
	}
	
}
