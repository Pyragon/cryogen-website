package com.cryo.modules.account.recovery;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.EmailConnection;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.PreviousConnection;
import com.cryo.db.impl.RecoveryConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.staff.entities.Recovery;
import com.cryo.utils.BCrypt;
import com.cryo.utils.CookieManager;
import com.cryo.utils.DateUtils;
import com.cryo.utils.EmailUtils;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;

import lombok.*;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: July 16, 2017 at 3:19:04 AM
 */
public class RecoveryModule extends WebModule {
	
	public static int EMAIL = 0, FORUM = 1;
	
	private String module;
	
	public RecoveryModule(Website website, String module) {
		super(website);
		this.module = module;
	}
	
	public String decodeRequest(Request request, Response response, RequestType type) {
		HashMap<String, Object> model = new HashMap<>();
		switch(module) {
			case "view_status":
				String id = request.queryParams("id");
				String success = request.queryParams("success");
				if (id == null || id.equals("")) {
					model.put("error", "no-id");
				} else if(success != null) {
					model.put("success", true);
					model.put("id", id);
					model.put("url", Website.PATH+"view_status?id="+id);
				} else {
					//GET&PUT RECOVERY STATUS
					Object[] data = RecoveryConnection.connection().handleRequest("get-recovery", id);
					if(data == null)
						model.put("error", "no-exist");
					else {
						Recovery recovery = (Recovery) data[0];
						int status = recovery.getActive();
						model.put("status", status);
						model.put("id", recovery.getId());
						model.put("url", Website.PATH+"view_status?id="+recovery.getId());
						if(status == 1)
							model.put("pass", recovery.getNewPass());
						else
							model.put("reason", recovery.getReason());
					}
				}
				return render("./source/modules/account/recovery/view_status.jade", model, request, response);
			case "recover":
				String action = request.queryParams("action");
				if(type == RequestType.GET) {
					
					if(action != null && action.equals("redeem_instant")) {
						String method = request.queryParams("method");
						String recov_id = request.queryParams("recovery_id");
						String instant_id = request.queryParams("instant_id");
						model = new HashMap<>();
						if(method == null || recov_id == null || instant_id == null) {
							model.put("error", "invalid");
							return render("./source/modules/account/recovery/redeem_instant.jade", model, request, response);
						}
						Object[] data = RecoveryConnection.connection().handleRequest("get-recovery", recov_id);
						if(data == null) {
							model.put("error", "no-recov");
							return render("./source/modules/account/recovery/redeem_instant.jade", model, request, response);
						}
						Recovery recovery = (Recovery) data[0];
						data = RecoveryConnection.connection().handleRequest("has-"+method+"-rec", recov_id);
						if(data == null) {
							model.put("error", "invalid");
							return render("./source/modules/account/recovery/redeem_instant.jade", model, request, response);
						}
						InstantRecoveryDAO instant = (InstantRecoveryDAO) data[0];
						if(!instant.getRand().equals(instant_id)) {
							model.put("error", "invalid");
							return render("./source/modules/account/recovery/redeem_instant.jade", model, request, response);
						}
						String new_pass = RandomStringUtils.random(15, true, true);
						String username = recovery.getUsername();
						try {
							RecoveryConnection.connection().handleRequest("set-status", recovery.getId(), 1, "");
							GlobalConnection.connection().handleRequest("change-pass", username, new_pass, null, false);
						} catch (Exception e) {
							e.printStackTrace();
							model.put("error", "error-recov");
							return render("./source/modules/account/recovery/redeem_instant.jade", model, request, response);
						}
						RecoveryConnection.connection().handleRequest("set-email-status", recovery.getId(), 1);
						RecoveryConnection.connection().handleRequest("set-forum-status", recovery.getId(), 1);
						model.put("success", true);
						model.put("newPass", new_pass);
						return render("./source/modules/account/recovery/redeem_instant.jade", model, request, response);
					} else if(action != null && action.equals("cancel")) {
						id = request.queryParams("recovery_id");
						String method = request.queryParams("method");
						String rand = request.queryParams("instant_id");
						model = new HashMap<>();
						while(true) {
							if(StringUtils.isNullOrEmpty(id) || StringUtils.isNullOrEmpty(method) || StringUtils.isNullOrEmpty(rand)) {
								model.put("success", false);
								model.put("error", "no-id");
								break;
							}
							Object[] data = RecoveryConnection.connection().handleRequest("get-recovery", id);
							if(data == null) {
								model.put("success", false);
								model.put("error", "invalid");
								break;
							}
							Recovery recovery = (Recovery) data[0];
							data = RecoveryConnection.connection().handleRequest("has-"+method+"-rec", recovery.getId());
							if(data == null) {
								model.put("success", false);
								model.put("error", "invalid-instant");
								break;
							}
							InstantRecoveryDAO instant = (InstantRecoveryDAO) data[0];
							if(!instant.getRand().equals(rand)) {
								model.put("success", false);
								model.put("error", "invalid-instant");
								break;
							}
							RecoveryConnection.connection().handleRequest("set-email-status", recovery.getId(), 1);
							RecoveryConnection.connection().handleRequest("set-forum-status", recovery.getId(), 1);
							RecoveryConnection.connection().handleRequest("set-status", recovery.getId(), 2, "Cancelled through "+method+".");
							model.put("success", true);
							break;
						}
						return render("./source/modules/account/recovery/cancel.jade", model, request, response);
					}
					if (request.queryParams().contains("username"))
						model.put("username", request.queryParams("username"));
					return render("./source/modules/account/recovery/recover.jade", model, request, response);
				} else {
					switch (action) {
						case "submit":
							Properties prop = new Properties();
							String username = request.queryParams("username");
							String email = request.queryParams("email");
							String forum = request.queryParams("forum");
							String creation = request.queryParams("creation");
							String cico = request.queryParams("cico");
							String additional = request.queryParams("additional");
							String[] passes = { request.queryParams("passone"), request.queryParams("passtwo"), request.queryParams("passthree") };
							String recovery_id = RandomStringUtils.random(20, true, true);
							long created = 0L;
							loop: while (true) {
								if (StringUtils.isNullOrEmpty(username)) {
									prop.put("success", false);
									prop.put("error", "Username must be filled out.");
									break loop;
								}
								if (allNullOrEmpty(email, forum, creation, cico, additional, passes[0], passes[1], passes[2])) {
									prop.put("success", false);
									prop.put("error", "At least one field must be filled out.");
									break loop;
								}
								Account account = AccountUtils.getAccount(username);
								if (account == null) {
									prop.put("success", false);
									prop.put("error", "No account of this name exists.");
									break loop;
								}
								if (!creation.equals("")) {
									// GET CREATION DATE (parsed), GET PROPER CREATION
									// DATE.
									// GET DIFF, SET RESPONSE TO VALUE. IN RECOVERY,
									// WE'LL
									// SHOW DIFF IN DAYS
									try {
										SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
										Date date = format.parse(creation);
										if (date == null) {
											prop.put("success", false);
											prop.put("error", "Creation date is typed incorrectly. Make sure it is in dd/MM/yyyy");
											break loop;
										}
										created = date.getTime();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								if (!email.equals("")) { // email verification
									Object[] data = EmailConnection.connection().handleRequest("get-email", username);
									if (data != null) {
										String real_email = (String) data[0];
										if (real_email.equalsIgnoreCase(email)) {
											// WE HAVE CORRECT EMAIL. LET'S SEND AN
											// EMAILED
											// RECOVERY
											EmailUtils.sendRecoveryEmail(username, recovery_id, real_email);
										}
									}
								}
								int forumId = 0;
								if (!forum.equals("")) { // forum verification
									if(!NumberUtils.isNumber(forum)) {
										prop.put("success", false);
										prop.put("error", "Forum ID must be a number!");
										break loop;
									}
									forumId = Integer.parseInt(forum);
									Object[] data = ForumConnection.connection().handleRequest("get-uid", username);
									if (data != null) {
										int real_id = (int) data[0];
										if (forumId == real_id) {
											// WE HAVE CORRECT FORUM ID. LET'S SEND A
											// PMED
											// RECOVERY
											String random = RandomStringUtils.random(20, true, true);
											RecoveryConnection.connection().handleRequest("add-forum-rec", recovery_id, random);
											String message = "Hello " + username
													+ ", a password recovery attempt has been made on your in-game account.\n\nIf you did not make this request, click here immediately!\n\nOtherwise follow this link to reset your password: http://cryogen-rsps.com/recover?action=redeem_instant&method=forum&recovery_id="+recovery_id+"&instant_id="+random;
											ForumConnection.sendForumMessage(real_id, "Recovery attempt made!", message, "Recovery Attempt");
										}
									}
								}
								Object[] data = PreviousConnection.connection().handleRequest("compare-hashes", passes, username);
								int[] res = new int[3];
								if (data == null)
									Arrays.fill(res, -1);
								else
									res = (int[]) data[0];
								String ip = request.ip();
								Recovery recovery = new Recovery(recovery_id, username, email, forumId, created, cico, additional, res, 0, "", "", request.ip(), new Timestamp(new Date().getTime()));
								RecoveryConnection.connection().handleRequest("add-recovery", recovery);
								prop.put("success", true);
								prop.put("url", Website.PATH+"/view_status?success=true&id="+recovery_id);
								break loop;
							}
							return new Gson().toJson(prop);
						default:
							return Website.render404(request, response);
					}
				}
			default:
				return Website.render404(request, response);
		}
	}
	
	public static boolean allNullOrEmpty(String... strings) {
		for (String s : strings)
			if (!StringUtils.isNullOrEmpty(s))
				return false;
		return true;
	}
	
}
