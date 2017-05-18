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
import com.cryo.modules.account.AccountDAO;
import com.cryo.modules.account.AccountUtils;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 13, 2017 at 1:36:30 PM
 */
public class EmailUtils {
	
	public static void sendVerificationEmail(String username, String email) {
		final String email_user = "pyragon.eldo@gmail.com";
		final String password = (String) Website.getProperties().get("emailpass");
		final String random = RandomStringUtils.random(20, true, true);
		final AccountDAO account = AccountUtils.getAccount(username);
		if(account == null)
			return;
		EmailConnection.connection().handleRequest("add-verify", username, email, random);
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email_user, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("webm@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(email));
			message.setSubject("Testing Subject");
			message.setText("Hello, "+AccountUtils.getDisplayName(account)+""
				+ "\n\nYour verification is nearly complete. Click or paste this link into your browser to verify your email! http://localhost:8181/account?action=verify&id="+random+"\""
						+ "\n\nThis link will expire in 24 hours.");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
