package com.cryo.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;

import com.cryo.Website;
import com.cryo.db.impl.EmailConnection;
import com.cryo.db.impl.RecoveryConnection;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.entities.Account;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 13, 2017 at 1:36:30 PM
 */
public class EmailUtils {
	
	public static void sendVerificationEmail(String username, String email) {
		final String random = RandomStringUtils.random(20, true, true);
		final Account account = AccountUtils.getAccount(username);
		if(account == null)
			return;
		EmailConnection.connection().handleRequest("add-verify", username, email, random);
		sendEmail(email, "Cryogen - Email verification almost complete!", "Hello, "+AccountUtils.getDisplayName(account)+"!"
				+ "\n\nYour verification is nearly complete. Click or paste this link into your browser to verify your email! http://cryogen.live/account?action=verify&id="+random+"\n\nThis link will expire in 24 hours.");
	}
	
	public static void sendRecoveryEmail(String username, String id, String email) {
		final String random = RandomStringUtils.random(20, true, true);
		final Account account = AccountUtils.getAccount(username);
		if(account == null)
			return;
		RecoveryConnection.connection().handleRequest("add-email-rec", id, random);
		sendEmail(email, "Cryogen - Password Recovery!",
				"Hello, "+AccountUtils.getDisplayName(account)+", a password recovery attempt has been made on your account.\n\nIf you did not make this request, click here immediately!\n\nOtherwise follow this link to reset your password: http://cryogen.live/recover?action=redeem_instant&method=email&recovery_id="+id+"&instant_id="+random+"\n\nThis link will expire in 24 hours.");
	}
	
	public static void sendEmail(String email, String subject, String message) {
		Session session;
		try {
			final String email_user = (String) Website.getProperties().get("email");
			final String password = (String) Website.getProperties().get("emailpass");
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "mail.privateemail.com");
			props.put("mail.smtp.port", "587");

			session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(email_user, password);
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}

		try {

			Message mime = new MimeMessage(session);
			mime.setFrom(new InternetAddress("noreply@cryogen.live"));
			mime.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(email));
			mime.setSubject(subject);
			mime.setText(message);

			try {
				Transport.send(mime);
			} catch(Exception e) {
				e.printStackTrace();
			}

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
