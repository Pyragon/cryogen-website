package com.cryo.server.item;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a single item.
 * <p>
 *
 * @author Graham / edited by Dragonkk(Alex)
 */
public class Item implements Serializable {

	private static final long serialVersionUID = -6485003878697568087L;

	protected short id;
	protected int amount, charges;
	
	protected @Setter @Getter String name, examine;

	protected boolean domItem;
	
	protected String uuid;

	public int getId() {
		return id;
	}

	@Override
	public Item clone() {
		return clone(id, amount);
	}

	public Item clone(int id) {
		return clone(id, amount);
	}

	public Item clone(int id, int amount) {
		return new Item(id, amount, domItem, uuid, false);
	}

	public Item(int id) {
		this(id, 1);
	}

	public Item(int id, int amount) {
		this(id, amount, false);
	}

	public Item(int id, int amount, boolean amt0) {
		this.id = (short) id;
		this.amount = amount;
		this.uuid = UUID.randomUUID().toString();
		if (this.amount <= 0 && !amt0) {
			this.amount = 1;
		}
	}

	public Item(int id, int amount, boolean dom_item, String uuid, boolean amt0) {
		this(id, amount, amt0);
		this.domItem = dom_item;
		this.uuid = uuid;
		if(this.uuid == null)
			this.uuid = UUID.randomUUID().toString();
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	public void set_id(int id) {
		this.id = (short) id;
	}

	public int getAmount() {
		return amount;
	}

	public boolean isDomItem() {
		return domItem;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", amount=" + amount + ", charges=" + charges + "] DomItem: "+domItem;
	}
	
	public String getUUID() {
		return uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Item))
			return super.equals(obj);
		Item item = (Item) obj;
		if(item.getUUID() == null || getUUID() == null) {
			System.out.println("Item: "+(item.getUUID() == null)+". This: "+(this.getUUID() == null));
			return false;
		}
		return this.getUUID().equals(item.getUUID());
	}

}
