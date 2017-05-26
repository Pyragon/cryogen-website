package com.cryo.paypal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.io.OutputStream;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.shop.InvoiceDAO;
import com.cryo.modules.account.shop.ShopItem;
import com.cryo.modules.account.shop.ShopManager;
import com.cryo.modules.account.shop.ShopUtils;
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
	
	public PaypalManager(Website website) {
		super(website);
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
	
	public void sendRedeem(String username, int package_id) throws IOException {
		OutputStream stream = new OutputStream();
		stream.writeByte(0);
		stream.writeString(username);
		stream.writeByte(package_id);
		stream.writeInt(1);
		Website.sendToServer(stream);
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
			InvoiceDAO invoice = ShopUtils.getInvoice(invoice_id, true, true);
			if(invoice == null)
				model.put("error", true);
			else {
				ShopUtils.updateItems(invoice.getUsername(), invoice.getItems());
			}
			return render("./source/modules/account/sections/shop/post_payment.jade", model, request, response);
		} catch (PayPalRESTException e) {
			return e.getDetails().toJSON();
		}
	}
	
}
