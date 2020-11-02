package com.cryo.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.cryo.Website;
import com.cryo.db.impl.ShopConnection;
import com.cryo.modules.account.entities.Invoice;
import com.cryo.modules.account.entities.ShopItem;
import com.cryo.managers.PaypalManager;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 17, 2017 at 1:05:37 AM
 */
public class PaypalTransaction {
	
	private HashMap<ShopItem, Integer> items;
	
	private static String ERROR_LINK = Website.getProperties().getProperty("path")+"paypal_error";

	private String link;
	
	private String username;
	
	private String invoice_id;
	
	private ItemList list;
	private Details details;
	private Amount amount;
	private Transaction transaction;
	private RedirectUrls redirect;
	private Payer payer;
	private Payment payment;
	
	public PaypalTransaction(String username, HashMap<Integer, Integer> items) {
		this.items = new HashMap<ShopItem, Integer>();
		for(Integer id : items.keySet()) {
			ShopItem item = ShopConnection.getShopItem(id);
			this.items.put(item, items.get(id));
		}
		this.username = username;
		this.invoice_id = createRandomInvoice();
		createPayer();
		createItemList();
		createDetails();
		createAmount();
		createRedirectURLs();
		createTransaction();
		createPayment();
		createInvoice();
		try {
			APIContext context = PaypalManager.getAPIContext();
			Payment created = payment.create(context);
			for(Iterator<Links> iterator = created.getLinks().iterator(); iterator.hasNext();) {
				Links link = iterator.next();
				if(link == null)
					continue;
				if(link.getRel().equalsIgnoreCase("approval_url"))
					this.link = link.getHref();
			}
		} catch(PayPalRESTException e) {
			e.printStackTrace();
			this.link = ERROR_LINK;
		}
	}
	
	public void createInvoice() {
		Invoice invoice = new Invoice(invoice_id, username, items, true, null);
		ShopConnection.connection().handleRequest("set-invoice", invoice);
	}
	
	public void createPayment() {
		payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setRedirectUrls(redirect);
		payment.setTransactions(new ArrayList<Transaction>() {
			private static final long serialVersionUID = 7068037613214763242L;
		{
			add(transaction);
		}});
	}
	
	public void createTransaction() {
		transaction = new Transaction();
		transaction.setAmount(amount);
		transaction.setItemList(list);
		transaction.setDescription("Purchasing items from the Cryogen shop");
		transaction.setInvoiceNumber(invoice_id);
	}
	
	public String createRandomInvoice() {
		String invoice = "";
		for(int i = 0; i < 15; i++) {
			invoice += Integer.toString(new Random().nextInt(10));
		}
		return invoice;
	}
	
	public void createPayer() {
		payer = new Payer();
		payer.setPaymentMethod("paypal");
	}
	
	public void createRedirectURLs() {
		//figure something out as these are GET requests, and I only use GET request to load index pages.
		//Will have to have special case for these 2 endpoints, or just leave the ones already in place.
		redirect = new RedirectUrls();
		redirect.setCancelUrl(Website.getProperties().getProperty("path")+"process_payment?action=cancel");
		redirect.setReturnUrl(Website.getProperties().getProperty("path")+"process_payment?action=process");
	}
	
	public void createItemList() {
		List<Item> items = new ArrayList<Item>();
		for(ShopItem shop_item : this.items.keySet()) {
			Integer quantity = this.items.get(shop_item);
			Item item = new Item();
			item.setName(shop_item.getName());
			item.setDescription(shop_item.getDescription());
			item.setCurrency("USD");
			item.setQuantity(quantity.toString());
			item.setPrice(Integer.toString(shop_item.getPrice()));
			items.add(item);
		}
		this.list = new ItemList();
		list.setItems(items);
	}
	
	public void createDetails() {
		details = new Details();
		details.setShipping("0");
		details.setTax("0");
		details.setSubtotal(Integer.toString(getTotal()));
	}
	
	public void createAmount() {
		amount = new Amount();
		amount.setCurrency("USD");
		amount.setTotal(Integer.toString(getTotal()));
		amount.setDetails(details);
	}
	
	public int getTotal() {
		int total = 0;
		for(ShopItem item : items.keySet()) {
			total += (item.getPrice() * items.get(item));
		}
		return total;
	}
	
	public String getLink() {
		return link;
	}
	
}
