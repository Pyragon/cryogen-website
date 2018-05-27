package com.cryo.paypal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.db.impl.ShopConnection;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Invoice;
import com.cryo.modules.account.entities.ShopItem;
import com.cryo.security.SessionIDGenerator;
import com.cryo.server.ServerConnection;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.NameValuePair;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import lombok.*;
import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: May 06, 2017 at 1:52:46 AM
 */
public class PaypalManager extends WebModule {
	
	private Gson gson;
	
	public PaypalManager(Website website) {
		super(website);
		gson = buildGson();
	}
	
	public Gson buildGson() {
		Gson gson = new GsonBuilder()
				.serializeNulls()
				.setVersion(1.0)
				.disableHtmlEscaping()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.setPrettyPrinting()
				.create();
		return gson;
	}
	
	private static APIContext context;
	
	public static void createAPIContext() {
		String client_id = Website.getProperties().getProperty("paypal-client");
		String secret = Website.getProperties().getProperty("paypal-secret");
		
		context = new APIContext(client_id, secret, "sandbox");
	}
	
	public static APIContext getAPIContext() {
		return context;
	}
	
	public String sendRedeem(String username, com.cryo.modules.account.entities.Package p) throws IOException {
		Properties prop = new Properties();
		prop.put("username", username);
		prop.put("package_id", p.getId());
		ServerConnection con = new ServerConnection("/redeem", prop);
		con.fetchData();
		if(con.failed())
			return con.getError();
		return null;
	}
	
	public String decodeRequest(Request request, Response response, RequestType type) {
		HashMap<String, Object> model = new HashMap<>();
		if (request.queryParams("action").equals("cancel")) {
			model.put("cancelled", true);
			return render("./source/modules/account/sections/shop/post_payment.jade", model, request, response);
		}
		String payer_id = request.queryParams("PayerID");
		String paymentId = request.queryParams("paymentId");
		
		Payment payment = new Payment();
		payment.setId(paymentId);
		
		PaymentExecution execution = new PaymentExecution();
		execution.setPayerId(payer_id);
		try {
			Payment created = payment.execute(context, execution);
			Transaction transaction = null;
			if(created.getTransactions() == null || created.getTransactions().size() == 0 || (transaction = created.getTransactions().get(0)) == null) {
				model.put("error", true);
				return render("./source/modules/account/sections/shop/post_payment.jade", model, request, response);
			}
			model.put("cancelled", false);
			String invoice_id = transaction.getInvoiceNumber();
			Object[] data = ShopConnection.connection().handleRequest("get-invoice", invoice_id, true);
			if(data == null) return null;
			Invoice invoice = (Invoice) data[0];
			if(invoice == null)
				model.put("error", true);
			else {
				for(ShopItem item : invoice.getItems().keySet()) {
					int quantity = invoice.getItems().get(item);
					for(int i = 0; i < quantity; i++) {
						com.cryo.modules.account.entities.Package packagee = new com.cryo.modules.account.entities.Package(-1, invoice.getUsername(), item.getId(), invoice.getInvoiceId(), true, null, null);
						ShopConnection.connection().handleRequest("add-package", packagee);
					}
				}
			}
			return render("./source/modules/account/sections/shop/post_payment.jade", model, request, response);
		} catch (PayPalRESTException e) {
			return e.getDetails().toJSON();
		}
	}
	
}
