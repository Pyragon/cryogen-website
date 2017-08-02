package com.cryo.modules.account.recovery;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.EmailConnection;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.PreviousConnection;
import com.cryo.db.impl.RecoveryConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountDAO;
import com.cryo.modules.account.AccountUtils;
import com.cryo.utils.CookieManager;
import com.cryo.utils.DateUtils;
import com.cryo.utils.EmailUtils;
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
			case "view-status":
				String id = request.queryParams("id");
				String success = request.queryParams("success");
				if (id == null || id.equals("")) {
					model.put("error", "no-id");
				} else if(success != null) {
					model.put("success", true);
					model.put("id", id);
				} else {
					//GET&PUT RECOVERY STATUS
					Object[] data = RecoveryConnection.connection().handleRequest("get-recovery", id);
					if(data == null)
						model.put("error", "no-exist");
					else {
						RecoveryDAO recovery = (RecoveryDAO) data[0];
						int status = recovery.getStatus();
						model.put("status", status);
						if(status == 2)
							model.put("new_pass", recovery.getNewPass());
					}
				}
				return render("./source/modules/account/recovery/view-status.jade", model, request, response);
			case "recover":
				String action = request.queryParams("action");
				if(type == RequestType.GET) {
					if(action != null) {
						switch(action) {
							case "view":
								id = request.queryParams("id");
								if(!CookieManager.isLoggedIn(request))
									return showLoginPage("recover?action=view&id="+id, request, response);
								AccountDAO account = CookieManager.getAccount(request);
								if(account == null) return redirect("/", request, response);
								int rights = account.getRights();
								while(true) {
									if(rights < 2) {
										model.put("norights", true);
										break;
									}
									if(id == null || id.equals("")) {
										model.put("success", false);
										break;
									}
									Object[] data = RecoveryConnection.connection().handleRequest("get-recovery", id);
									if(data == null) {
										model.put("success", false);
										break;
									}
									RecoveryDAO recovery = (RecoveryDAO) data[0];
									model.put("success", true); //we have a recovery.
									model.put("recovery", recovery); //used to display the information
									//GET RESULTS TO ANSWERS
									String username = recovery.getUsername();
									//EMAIL FIRST
									String entered_email = recovery.getEmail();
									data = EmailConnection.connection().handleRequest("get-email", username);
									model.put("has_email", (data != null));
									if(data != null) {
										String real_email = (String) data[0];
										model.put("real_email", real_email);
										model.put("correct_email", real_email.equalsIgnoreCase(entered_email));
									}
									//FORUM ID
									String forum_id = recovery.getForumId();
									data = ForumConnection.connection().handleRequest("get-uid", username);
									model.put("has_forum", (data != null));
									if(data != null) {
										int real_id = (int) data[0];
										model.put("forum_id", Integer.toString(real_id));
										model.put("correct_forum", Integer.toString(real_id).equals(forum_id));
									}
									//CREATION DATE
									long l = recovery.getCreation();
									if(l != 0L) {
										Date date = new Date(l);
										Date created = account.getCreationDate();
										long days_off = DateUtils.getDateDiff(date, created, TimeUnit.DAYS);
										if(days_off < 0)
											days_off = -days_off;
										model.put("days_off", days_off);
									}
									//CITY/COUNTRY
									model.put("cico", recovery.getCico());
									break;
								}
								return render("./source/modules/account/recovery/view-recovery.jade", model, request, response);
						}
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
								AccountDAO account = AccountUtils.getAccount(username);
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
								if (!forum.equals("")) { // forum verification
									Object[] data = ForumConnection.connection().handleRequest("get-uid", username);
									if (data != null) {
										int real_id = (int) data[0];
										if (forum.equals(Integer.toString(real_id))) {
											// WE HAVE CORRECT FORUM ID. LET'S SEND A
											// PMED
											// RECOVERY
											String random = RandomStringUtils.random(20, true, true);
											RecoveryConnection.connection().handleRequest("add-forum-rec", recovery_id, random);
											String message = "Hello " + username
													+ ", a password recovery attempt has been made on your in-game account.\n\nIf you did not make this request, click here immediately!\n\nOtherwise follow this link to reset your password: http://cryogen-rsps.com/recover_final?method=email&id="
													+ random;
											ForumConnection.sendForumMessage(real_id, "Recovery attempt made!", message);
										}
									}
								}
								Object[] data = PreviousConnection.connection().handleRequest("compare-hashes", passes, username);
								int[] res = new int[3];
								if (data == null)
									Arrays.fill(res, -1);
								else
									res = (int[]) data[0];
								RecoveryDAO recovery = new RecoveryDAO(recovery_id, username, email, forum, created, cico, additional, res, 0, "");
								RecoveryConnection.connection().handleRequest("add-recovery", recovery);
								prop.put("success", true);
								String html = redirect("/view-status?success=true&id="+recovery_id, 0, request, response);
								prop.put("html", html);
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
