package com.cryo.modules.account.support.punish;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.cryo.db.DatabaseConnection;
import com.cryo.db.SQLQuery;
import com.cryo.db.impl.PunishmentsConnection;
import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.account.entities.Appeal;
import com.cryo.modules.account.entities.Punishment;
import com.cryo.modules.staff.BugReport;
import com.cryo.modules.staff.PlayerReport;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 22, 2017 at 2:34:49 AM
 */
public class PunishUtils {
	
	public ArrayList<Punishment> getPunishments(String username, boolean archive) {
		return getPunishments(username, archive, 0);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Punishment> getPunishments(String username, boolean archive, int page) {
		Object[] data = PunishmentsConnection.connection().handleRequest("get-punishments", username, archive, page);
		if(data == null)
			return new ArrayList<Punishment>();
		return (ArrayList<Punishment>) data[0];
	}
	
	public static int getTotalPunishmentPages(String username, boolean archived) {
		 try {
			 Object[] data = PunishmentsConnection.connection().handleRequest("get-total-punish-results", username, archived);
			 if(data == null) return 1;
			 int results = (int) data[0];
			 return results;
		 } catch(Exception e) {
			 e.printStackTrace();
		 }
		 return 1;
	}
	
	public Punishment getPunishmentFromAppeal(int appealId) {
		Object[] data = PunishmentsConnection.connection().handleRequest("get-punishment-from-appeal", appealId);
		if(data == null)
			return null;
		return (Punishment) data[0];
	}
	
	public static Punishment getPunishment(int pid) {
		Object[] data = PunishmentsConnection.connection().handleRequest("get-punishment", pid);
		if(data == null)
			return null;
		return (Punishment) data[0];
	}
	
	public Appeal getAppealFromPunishment(int punishment) {
		Object[] data = PunishmentsConnection.connection().handleRequest("get-punishment", punishment);
		if(data == null)
			return null;
		Punishment punish = (Punishment) data[0];
		int appealId = punish.getAppealId();
		data = PunishmentsConnection.connection().handleRequest("get-appeal", appealId, false);
		if(data == null)
			return null;
		return (Appeal) data[0];
	}
	
	public Appeal getAppeal(int appealId) {
		Object[] data = PunishmentsConnection.connection().handleRequest("get-appeal", appealId, false);
		if(data == null)
			return null;
		return (Appeal) data[0];
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ACommentDAO> getComments(int appealId, int type) {
		Object[] data = PunishmentsConnection.connection().handleRequest("get-comments", appealId, type);
		if(data == null)
			return null;
		return (ArrayList<ACommentDAO>) data[0];
	}
	
	public static void createAppeal(int punishId, String username, String title, String detailed) {
		Punishment punish = getPunishment(punishId);
		if(punish == null) return;
		//Appeal appeal = new Appeal(0, username, title, detailed, 0, punishId, null);
		//PunishmentConnection.connection().handleRequest("create-appeal", appeal);
	}
	
	public ArrayList<PlayerReport> getPlayerReports(String username, boolean archived) {
		return getPlayerReports(username, archived, 0);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<PlayerReport> getPlayerReports(String username, boolean archived, int page) {
		ArrayList<PlayerReport> reports = new ArrayList<>();
		Object[] data = ReportsConnection.connection().handleRequest("get-player-reports", archived, page);
		if(data != null) {
			for(PlayerReport report : (ArrayList<PlayerReport>) data[0]) {
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
	
	public ArrayList<BugReport> getBugReports(String username, boolean archived) {
		return getBugReports(username, archived, 0);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<BugReport> getBugReports(String username, boolean archived, int page) {
		ArrayList<BugReport> reports = new ArrayList<>();
		Object[] data = ReportsConnection.connection().handleRequest("get-bug-reports", archived, page);
		if(data != null) {
			for(BugReport report : (ArrayList<BugReport>) data[0]) {
				if(username != null && report.userHasRead(username))
					continue;
				reports.add(report);
			}
		}
		return reports;
	}
	
	public ArrayList<Appeal> getAppeals(String username, boolean archived) {
		return getAppeals(username, archived, 0);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Appeal> getAppeals(String username, boolean archived, int page) {
		ArrayList<Appeal> appeals = new ArrayList<>();
		Object[] data = PunishmentsConnection.connection().handleRequest("get-appeals", archived, page);
		if(data != null) {
			for(Appeal appeal : (ArrayList<Appeal>) data[0]) {
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
		DatabaseConnection connection = type == ReportType.APPEAL ? PunishmentsConnection.connection() : ReportsConnection.connection();
		Object[] data = connection.select(table, "id=?", connection.GET_READ, id);
		if(data == null) return;
		String read = (String) data[0];
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
