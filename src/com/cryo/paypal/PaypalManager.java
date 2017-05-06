package com.cryo.paypal;

import java.io.IOException;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
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
	
	static {
		createAPIContext();
	}
	
	public PaypalManager(Website website) {
		super(website);
	}
	
	private static APIContext context;
	
	public static APIContext createAPIContext() {
		String client_id = Website.getProperties().getProperty("paypal-client");
		String secret = Website.getProperties().getProperty("paypal-secret");
		
		APIContext context = new APIContext(client_id, secret, "sandbox");
		return context;
	}
	
	public static APIContext getAPIContext() {
		return context;
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
			
			model.put("cancelled", false);
			return render("./source/modules/account/sections/shop/post_payment.jade", model, request, response);
		} catch (PayPalRESTException e) {
			return e.getDetails().toJSON();
		}
	}
	
}
