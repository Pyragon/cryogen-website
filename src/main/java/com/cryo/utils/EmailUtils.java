package com.cryo.utils;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.modules.account.AccountUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {

	public static void sendVerificationEmail(String username, String email) {
		final String random = RandomStringUtils.random(20, true, true);
		final Account account = AccountUtils.getAccount(username);
		if(account == null)
			return;
		sendEmail(email, "Cryogen - Email verification almost complete!", "Hello, "+ account.getDisplayName()+"!"
				+ "\n\nYour verification is nearly complete. Click or paste this link into your browser to verify your email! http://cryogen.live/account?action=verify&id="+random+"\n\nThis link will expire in 24 hours.");
	}
	
	public static void sendEmail(String to, String subject, String message) {
		Session session;
		try {
			final String from = (String) Website.getProperties().get("email");
			final String password = (String) Website.getProperties().get("email_pass");
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "mail.privateemail.com");
			props.put("mail.smtp.port", "587");

			session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from, password);
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}

		try {

			Message mime = new MimeMessage(session);
			mime.setFrom(new InternetAddress("noreply@cryogen-rsps.com"));
			mime.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
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
