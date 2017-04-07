package com.cryo.modules.staff.playerreports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.db.impl.ReportsConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.Account;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.support.PlayerReportDAO;
import com.cryo.modules.account.support.punish.PunishUtils;
import com.cryo.utils.CommentDAO;
import com.cryo.utils.DateFormatter;
import com.cryo.utils.Utilities;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: April 06, 2017 at 10:13:07 PM
 */
public class PlayerReportsModule {
	
	@SuppressWarnings("unchecked")
	public static ArrayList<CommentDAO> getComments(int id) {
		ArrayList<CommentDAO> comments = new ArrayList<>();
		Object[] data = ReportsConnection.connection().handleRequest("get-comments", id, 0);
		if(data == null)
			return comments;
		return (ArrayList<CommentDAO>) data[0];
	}
	
	public static Properties handleRequest(String action, Request request, Response response, Properties prop, WebModule module) {
		switch(action) {
			case "view-list":
				HashMap<String, Object> model = new HashMap<>();
				model.put("preports", new PunishUtils().getPlayerReports(null));
				model.put("utils", new PunishUtils());
				model.put("formatter", new DateFormatter());
				prop.put("success", true);
				prop.put("html", module.render("./source/modules/staff/player_reports/report_list.jade", model, request, response));
				break;
			case "view-report":
				model = new HashMap<>();
				int id = Integer.parseInt(request.queryParams("id"));
				Object[] data = ReportsConnection.connection().handleRequest("get-player-report", id);
				if(data == null) {
					prop.put("success", false);
					prop.put("error",  "Report not found.");
					return prop;
				}
				PlayerReportDAO report = (PlayerReportDAO) data[0];
				model.put("report",  report);
				model.put("comments", getComments(report.getId()));
				String html = module.render("./source/modules/staff/player_reports/view_report.jade", model, request, response);
				Account account = AccountUtils.getAccount(report.getUsername());
				String name = AccountUtils.crownHTML(account);
				prop.put("success", true);
				prop.put("html", html);
				prop.put("display", name);
				break;
			case "submit-com":
				id =  Integer.parseInt(request.queryParams("id"));
				String username = request.session().attribute("cryo-user");
				String comment = request.queryParams("comment");
				data = ReportsConnection.connection().handleRequest("submit-com", id, 0, username, comment);
				if(data == null) {
					prop.put("success", false);
					prop.put("error", "Error submitting comment. Please try again later and contact an Admin if the problem persists.");
					break;
				}
				model = new HashMap<>();
				model.put("comments", getComments(id));
				html = module.render("./source/modules/utils/comments.jade", model, request, response);
				prop.put("success", true);
				prop.put("comments", html);
				break;
		}
		return prop;
	}
	
}
