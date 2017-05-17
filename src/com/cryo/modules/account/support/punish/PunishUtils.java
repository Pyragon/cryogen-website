package com.cryo.modules.account.support.punish;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.cryo.db.DatabaseConnection;
import com.cryo.db.impl.PunishmentConnection;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.account.support.BugReportDAO;
import com.cryo.modules.account.support.PlayerReportDAO;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 22, 2017 at 2:34:49 AM
 */
public class PunishUtils {
	
	@SuppressWarnings("unchecked")
	public ArrayList<PunishDAO> getPunishments(String username, boolean archive) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-punishments", username, archive);
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
	
	public PunishDAO getPunishment(int pid) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-punishment", pid);
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
		data = PunishmentConnection.connection().handleRequest("get-appeal", appealId, false);
		if(data == null)
			return null;
		return (AppealDAO) data[0];
	}
	
	public AppealDAO getAppeal(int appealId) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-appeal", appealId, false);
		if(data == null)
			return null;
		return (AppealDAO) data[0];
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ACommentDAO> getComments(int appealId, int type) {
		Object[] data = PunishmentConnection.connection().handleRequest("get-comments", appealId, type);
		if(data == null)
			return null;
		return (ArrayList<ACommentDAO>) data[0];
	}
	
	public static void createAppeal(int punishId, String username, String title, String detailed) {
		AppealDAO appeal = new AppealDAO(0, username, title, detailed, 0, punishId, null);
		PunishmentConnection.connection().handleRequest("create-appeal", appeal);
	}
	
	public ArrayList<PlayerReportDAO> getPlayerReports(String username, boolean archived) {
		return getPlayerReports(username, archived, 0);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<PlayerReportDAO> getPlayerReports(String username, boolean archived, int page) {
		ArrayList<PlayerReportDAO> reports = new ArrayList<>();
		Object[] data = ReportsConnection.connection().handleRequest("get-player-reports", archived, page);
		if(data != null) {
			for(PlayerReportDAO report : (ArrayList<PlayerReportDAO>) data[0]) {
				if(username != null && report.userHasRead(username))
					continue;
				reports.add(report);
			}
		}
		return reports;
	}
	
	public static int getTotalPages(DatabaseConnection connection, String table) {
		Object[] data = connection.handleRequest("get-total-results", table);
		if(data == null)
			return 0;
		int total = (int) data[0];
		total = (int) Utilities.roundUp(total, 10);
		return total;
	}
	
	public ArrayList<BugReportDAO> getBugReports(String username, boolean archived) {
		return getBugReports(username, archived, 0);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<BugReportDAO> getBugReports(String username, boolean archived, int page) {
		ArrayList<BugReportDAO> reports = new ArrayList<>();
		Object[] data = ReportsConnection.connection().handleRequest("get-bug-reports", archived, page);
		if(data != null) {
			for(BugReportDAO report : (ArrayList<BugReportDAO>) data[0]) {
				if(username != null && report.userHasRead(username))
					continue;
				reports.add(report);
			}
		}
		return reports;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<AppealDAO> getAppeals(String username, boolean archived) {
		ArrayList<AppealDAO> appeals = new ArrayList<>();
		Object[] data = PunishmentConnection.connection().handleRequest("get-appeals", archived);
		if(data != null) {
			for(AppealDAO appeal : (ArrayList<AppealDAO>) data[0]) {
				if(username != null && appeal.userHasRead(username))
					continue;
				appeals.add(appeal);
			}
		}
		return appeals;
	}
	
	@SuppressWarnings("unchecked")
	public static void pinReport(int id, String username, ReportType type) {
		String table = "reports";
		switch(type) {
			case APPEAL:
				table = "appeals";
				break;
			case PLAYER:
				table = "player_reports";
				break;
			default:
				table = "bug_reports";
				break;
		}
		DatabaseConnection connection = type == ReportType.APPEAL ? PunishmentConnection.connection() : ReportsConnection.connection();
		ResultSet set = connection.select(table, "id=?", id);
		if(connection.empty(set))
			return;
		String read = connection.getString(set, "read");
		ArrayList<String> list = new ArrayList<String>();
		if(!read.equals(""))
			list = new Gson().fromJson(read, ArrayList.class);
		if(!list.contains(username))
			list.add(username);
		else
			list.remove(username);
		connection.set(table, "`read`=?", "id=?", new Gson().toJson(list), id);
	}
	
	public static enum ReportType {
		APPEAL, PLAYER, BUG;
		
		public static Optional<ReportType> getType(String type) {
			return Arrays.stream(values()).filter(t -> t.name().equalsIgnoreCase(type)).findAny();
		}
	}
	
}
